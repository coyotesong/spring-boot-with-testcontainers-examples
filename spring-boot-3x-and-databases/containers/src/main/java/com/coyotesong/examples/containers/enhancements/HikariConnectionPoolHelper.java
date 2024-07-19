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

package com.coyotesong.examples.containers.enhancements;

import com.coyotesong.examples.containers.utility.MyMetricsTracker;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.*;

/**
 * Hikari tamer
 *
 * This class is an attempt to eliminate multiple "FATAL: terminating connection due to unexpected postmaster exit"
 * errors from the container as it is shut down. This appears to be due to the Hikari pools being left
 * open as the application shuts down.
 *
 * I added `@Bean(destroyMethod = "close")` to the DataSource but that was insufficient to solve the
 * problem.
 */
@SuppressWarnings("JavadocBlankLines")
public class HikariConnectionPoolHelper implements MetricsTrackerFactory, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(HikariConnectionPoolHelper.class);

    public static final HikariConnectionPoolHelper INSTANCE = new HikariConnectionPoolHelper();

    private final Map<String, HikariDataSource> hikariDataSources = new LinkedHashMap<>();
    private final Map<String, HikariPoolMXBean> hikariPoolMXBeans = new LinkedHashMap<>();
    private final Map<String, IMetricsTracker> trackers = new LinkedHashMap<>();

    // private final List<HikariConfigMXBean> hikariConfigMXBeans = new ArrayList<>();

    // should this use weak connections?
    private final List<Connection> connections = new ArrayList<>();

    /**
     * Singleton constructor
     */
    private HikariConnectionPoolHelper() {
    }

    /**
     * Create an instance of an IMetricsTracker.
     *
     * @param poolName  the name of the pool
     * @param poolStats a PoolStats instance to use
     * @return a IMetricsTracker implementation instance
     */
    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        IMetricsTracker tracker = new MyMetricsTracker(poolName, poolStats);
        trackers.put(poolName, tracker);
        return tracker;
    }

    /**
     * Create a new connection
     *
     * This method allows us to maintain a cache of connections in order to ensure
     * they're they're properly terminated.
     *
     * @param driverInstance java.sql.Driver instance
     * @param jdbcUrl        JDBC url
     * @param connInfo       connection information (username, password)
     * @return new connection
     * @throws SQLException a problem occurred
     */
    public Connection connect(Driver driverInstance, String jdbcUrl, Properties connInfo) throws SQLException {
        final Connection conn = driverInstance.connect(jdbcUrl, connInfo);
        connections.add(conn);
        return conn;
    }

    /**
     * Create a Hikari DataSource
     *
     * This method does two important things. First, it eliminates duplicate dataSources
     * (with identical driver, URL, username, and password properties). This helps ensure
     * all connections are accounted for.
     *
     * Second, it tracks the HikariPoolMXBeans so we can perform a clean shutdown later.
     * We could also do this via JMX but we don't have to rely on that if we capture the
     * information here.
     *
     * @param config Hikari configuration
     * @return Hikari Datasource
     */
    public HikariDataSource getDataSource(HikariConfig config) {
        // Do we already have a matching entry?
        // IMPORTANT: we might need to soften this when we add more advanced authentication mechanisms!
        for (HikariDataSource ds : hikariDataSources.values()) {
            // reminder: username or password may be null!
            if (ds.getDriverClassName().equals(config.getDriverClassName()) &&
                    ds.getJdbcUrl().equals(config.getJdbcUrl()) &&
                    Objects.equals(ds.getUsername(), config.getUsername()) &&
                    Objects.equals(ds.getPassword(), config.getPassword())) {

                LOG.info("reusing existing dataSource: {}", ds.getPoolName());
                return ds;
            }
        }

        config.setRegisterMbeans(true);
        config.setAllowPoolSuspension(true);
        config.setMetricsTrackerFactory(INSTANCE);

        final HikariDataSource ds = new HikariDataSource(config);
        final String poolName = ds.getPoolName();

        if (!hikariDataSources.containsKey(poolName)) {
            hikariDataSources.put(poolName, ds);
        } else {
            LOG.warn("already had hikariDataSource for {}!", poolName);
        }

        if (!hikariPoolMXBeans.containsKey(poolName)) {
            hikariPoolMXBeans.put(poolName, ds.getHikariPoolMXBean());
        } else {
            LOG.warn("already had hikariMXBean for {}!", poolName);
        }

        return ds;
    }

    /**
     * Count the number of connections and perform a soft eviction.
     *
     * @param beans HikariPoolMXBeans
     * @return number of total and pending connections
     */
    @SuppressWarnings("UnusedReturnValue")
    public int countConnections(Map<ObjectName, HikariPoolMXBean> beans) {
        int total = 0;
        for (Map.Entry<ObjectName, HikariPoolMXBean> entry : beans.entrySet()) {
            System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "100");
            try {
                final ObjectName poolName = entry.getKey();
                final HikariPoolMXBean bean = entry.getValue();
                if (bean != null) {
                    LOG.debug("pool size ({}) --> active: {} idle: {} total: {} awaiting: {}",
                            poolName, bean.getActiveConnections(), bean.getIdleConnections(), bean.getTotalConnections(), bean.getThreadsAwaitingConnection());
                    total += bean.getTotalConnections() + bean.getThreadsAwaitingConnection();
                    bean.softEvictConnections();
                    try {
                        bean.suspendPool();
                    } catch (Exception e) {
                        LOG.info("{}: error when accessing HikariPoolMXBean: {}", e.getClass().getName(), e.getMessage());
                    }
                }
            } finally {
                System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
            }
        }

        return total;
    }

    /**
     * Close all connections
     */
    @Override
    public void close() {
        for (Connection conn : connections) {
            try {
                if ((conn != null) && !conn.isClosed()) {
                    LOG.info("a connection was left open");
                    conn.close();
                }
            } catch (SQLException e) {
                LOG.warn("{}: failure when checking connection status: {}", e.getClass().getName(), e.getMessage());
            }
        }
        connections.clear();

        final Map<ObjectName, HikariPoolMXBean> beans = new LinkedHashMap<>();
        for (Map.Entry<String, HikariPoolMXBean> entry : hikariPoolMXBeans.entrySet()) {
            if (entry.getValue() == null) {
                // this shouldn't happen
                LOG.warn("bean: {} -> null", entry.getKey());
            } else {
                try {
                    // LOG.info("bean: {} -> {}", entry.getKey(), entry.getValue());
                    final ObjectName name = new ObjectName("com.zaxxer.hikari:type=Pool (" + entry.getKey() + ")");
                    beans.put(name, entry.getValue());
                } catch (MalformedObjectNameException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        countConnections(beans);

        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        for (Map.Entry<ObjectName, HikariPoolMXBean> entry : beans.entrySet()) {
            try {
                mBeanServer.unregisterMBean(entry.getKey());
            } catch (InstanceNotFoundException e) {
                LOG.info("{}: already unregistered", entry.getKey());
            } catch (MBeanRegistrationException e) {
                LOG.info("{}: {}", e.getClass().getName(), e.getMessage());
            }
        }

        for (IMetricsTracker tracker : trackers.values()) {
            tracker.close();
        }
        trackers.clear();

        hikariPoolMXBeans.clear();
    }
}
