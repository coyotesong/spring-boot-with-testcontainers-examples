/*
 * Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coyotesong.examples.containers.traits;

import com.coyotesong.examples.containers.EnhancedSlf4jLogConsumer;
import com.coyotesong.examples.containers.GuestOSDetails;
import com.coyotesong.examples.containers.PostConstructAction;
import com.coyotesong.examples.containers.enhancements.DataSourceHelper;
import com.coyotesong.examples.containers.enhancements.GuestOSDetailsHelper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenience interface that adds a Hikari-based getDataSource() method to the container.
 *
 * This is important for two reasons. First, it allows us to (in theory) test our monitoring tools
 * in addition to the code itself. At the moment the standard Hikari metric tracker implementations
 * include
 *
 * - DropWizard [CodaHaleMetricsTracker](https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/dropwizard/CodaHaleMetricsTracker.java)
 * - Micrometer [MicrometerMetricsTracker](https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/micrometer/MicrometerMetricsTracker.java)
 * - Prometheus [PrometheusHistogramMetricsTracker](https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/prometheus/PrometheusHistogramMetricsTracker.java)
 * - Prometheus [PrometheusMetricstracker](https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/prometheus/PrometheusMetricsTracker.java)
 *
 * Second, I'm seeing an unpredictable number of warnings from the guest database that not all connections
 * have closed when the server is shut down. Using Hikari makes it much easier to investigate this
 * issue since it can track all database connections.
 */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public interface JdbcDatabaseContainerEnhancements<SELF extends JdbcDatabaseContainer<SELF>> extends JdbcDatabaseAware {
    ThreadLocal<EnhancedSlf4jLogConsumer<? extends BaseConsumer<?>>> logConsumer = new ThreadLocal<>();
    ThreadLocal<Logger> loggers = new ThreadLocal<>();
    ThreadLocal<DataSourceHelper<?>> dataSourceHelper = new ThreadLocal<>();
    ThreadLocal<GuestOSDetailsHelper<?>> guestOSDetailsHelper = new ThreadLocal<>();
    ThreadLocal<List<PostConstructAction<JdbcDatabaseContainer<?>, ?>>> postConstructActions = new ThreadLocal<>();

    /**
     * Reset all enhancements
     */
    default void resetEnhancements() {
        logConsumer.remove();
        loggers.remove();
        guestOSDetailsHelper.remove();
        dataSourceHelper.remove();
        postConstructActions.remove();
    }

    /**
     * Reset all enhancements, register enhanced log consumer.
     *
     * @param dockerImageName docker image name
     */
    default void resetEnhancements(DockerImageName dockerImageName) {
        resetEnhancements();
        final String logname = "Container." + dockerImageName.asCanonicalNameString().replace(":", "$");
        logConsumer.set(new EnhancedSlf4jLogConsumer<>(LoggerFactory.getLogger(logname)));
    }

    /**
     * Add post-construct action
     *
     * @param action post-construct action
     */
    default void addPostConstructAction(PostConstructAction<JdbcDatabaseContainer<?>, ?> action) {
        if (postConstructActions.get() == null) {
            postConstructActions.set(new ArrayList<>());
        }
        postConstructActions.get().add(action);
    }

    /**
     * Actions performed after the server has started
     *
     * @param container test container being enhanced
     */
    default void postConstruct(JdbcDatabaseContainer<SELF> container) {
        if (loggers.get() == null) {
            loggers.set(LoggerFactory.getLogger(container.getClass().getName()));
        }

        if (dataSourceHelper.get() == null) {
            dataSourceHelper.set(new DataSourceHelper<>(container));
        }

        if (guestOSDetailsHelper.get() == null) {
            guestOSDetailsHelper.set(new GuestOSDetailsHelper<>(container));
        }

        // start to pass through routine messages
        if (logConsumer.get() != null) {
            logConsumer.get().setDisableStdout(false);
        }

        if (loggers.get() != null) {
            loggers.get().info("postConstruct()...");
        }

        // add details about Guest OS and server to the log
        if (guestOSDetailsHelper.get() != null) {
            loggers.get().info("database properties\n{}", guestOSDetailsHelper.get().timestamp());
        }

        // perform post-construct actions
        if (dataSourceHelper.get() != null) {
            dataSourceHelper.get().postConstruct();

            if (postConstructActions.get() != null) {
                for (PostConstructAction<JdbcDatabaseContainer<?>, ?> action : postConstructActions.get()) {
                    dataSourceHelper.get().apply(action);
                }
            }
        }
    }

    /**
     * Actions performed before the server is stopped
     */
    default void preDestroy() {
        if (dataSourceHelper.get() != null) {
            dataSourceHelper.get().preDestroy();
        }

        if (logConsumer.get() != null) {
            logConsumer.get().setDisableStdout(true);
        }

        if (loggers.get() != null) {
            loggers.get().info("preDestroy()...");
        }

        resetEnhancements();
    }

    /**
     * Get single-use connection (for initial configuration)
     *
     * @return single use Connection
     * @throws SQLException a problem occurred
     */
    default Connection getConnection() throws SQLException {
        return dataSourceHelper.get().getConnection();
    }

    /**
     * Get Hikari DataSource
     *
     * This is an improvement over a standard DataSource since it's Autocloseable.
     *
     * @return new dataSource
     */
    default HikariDataSource getDataSource() {
        return dataSourceHelper.get().getDataSource();
    }

    /**
     * Get GuestOSDetails
     *
     * @return guest OS details
     */
    default GuestOSDetails getGuestOSDetails() throws IOException, SQLException {
        return guestOSDetailsHelper.get().getGuestDetails();
    }
}
