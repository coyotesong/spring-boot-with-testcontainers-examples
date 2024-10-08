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

package com.coyotesong.examples.containers.actions;

import com.coyotesong.examples.containers.PostConstructAction;
import com.coyotesong.examples.containers.traits.JdbcDatabaseContainerEnhancements;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * PostConstruction action that performs flyway migration of the database schema.
 *
 * Implementation notes:
 * - the container must provide a `getDataSource()` method.
 * - we can't use a spring DatabasePopulator since that interface gets a `Connection` and
 *   the flyway `configure()` method requires a `DataSource`.
 */
@SuppressWarnings("JavadocBlankLines")
public class FlywayMigrationAction implements PostConstructAction<JdbcDatabaseContainer<?>, Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(FlywayMigrationAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Use flyway to migrate database schema";
    }

    /**
     * Use Flyway to populate the database schema
     */
    @Override
    public Boolean apply(JdbcDatabaseContainer<?> container) {
        if (container instanceof JdbcDatabaseContainerEnhancements<?> enhanced) {
            try (HikariDataSource dataSource = enhanced.getDataSource()) {
                final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
                final MigrateResult result = flyway.migrate();
                return result.success;
            }
        }

        LOG.info("Container does not provide 'container.getDataSource())");
        return false;
    }
}
