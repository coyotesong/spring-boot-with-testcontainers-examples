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

package com.coyotesong.examples.containers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * PostgreSQL test container extended with support for flyway initialization
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public class PostgreSQLContainerWithFlyway<T extends PostgreSQLContainer<T>> extends PostgreSQLContainer<T> {
    private static final Logger LOG = LoggerFactory.getLogger(PostgreSQLContainerWithFlyway.class);

    public PostgreSQLContainerWithFlyway(String dockerImageName) {
        super(dockerImageName);
    }

    /**
     * Get Hikari DataSource
     * <p>
     * Get Hikari DataSource - this is an improvement over a standard datasource
     * since it's auto-closeable.
     *
     * @return new dataSource
     */
    public HikariDataSource getDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setDriverClassName(getDriverClassName());
        config.setConnectionTestQuery(getTestQueryString());

        return new HikariDataSource(config);
    }

    /**
     * Initialize schemas (via flyway)
     */
    @Override
    protected void runInitScriptIfRequired() {
        // Add our log consumer. The entire List is replaced since
        // there's the possibility that the list is immutable.
        // (Collections.emptyList() or Collections.singletonList())
        final List<Consumer<OutputFrame>> consumers = new ArrayList<>();
        if (!super.getLogConsumers().isEmpty()) {
            consumers.addAll(super.getLogConsumers());
        }

        final Logger log = LoggerFactory.getLogger("Container." + super.getDockerImageName().replace(":", "$"));
        consumers.add(new MySlf4jLogConsumer<>(log));
        super.setLogConsumers(consumers);

        // initialize schemas and initialized data via flyway
        // we can't use a spring DatabaePopulator since that interface gets a `Connection` and
        // the flyway `configure()` method requires a `DataSource`.
        try (HikariDataSource dataSource = getDataSource()) {
            final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
            final MigrateResult result = flyway.migrate();
        }

        super.runInitScriptIfRequired();
    }
}
