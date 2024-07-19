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

package com.coyotesong.examples.persistence.config;

import com.coyotesong.examples.persistence.jooq.I18nLanguageRepositoryJooq;
import com.coyotesong.examples.persistence.jooq.I18nRegionRepositoryJooq;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.testcontainers.shaded.com.google.common.base.Splitter;

import javax.sql.DataSource;
import java.util.List;

/**
 * Configure jooq-related properties
 */
@Configuration
@Import({
        I18nLanguageRepositoryJooq.class,
        I18nRegionRepositoryJooq.class
})
@Profile("jooq")
public class TestJooqProperties {

    /**
     * Get jOOQ SQLDialect based on connection info
     *
     * @param connectionDetails connection info
     * @return jOOQ SQLDialect
     */
    @Bean
    public SQLDialect dialect(@Autowired JdbcConnectionDetails connectionDetails) {
        final List<String> components = Splitter.on(':').splitToList(connectionDetails.getJdbcUrl());
        if (components.size() < 2) {
            return SQLDialect.DEFAULT;
        }

        // note: cockroachdb reports 'postgresql'
        // note: questdb reports 'postgresql'
        // note: tidb reports 'mysql'
        // note: timescale reports 'postgresql'
        switch (components.get(1).toLowerCase()) {
            case "mariadb":
                return SQLDialect.MARIADB;
            case "mysql":
                return SQLDialect.MYSQL;
            case "postgresql":
                return SQLDialect.POSTGRES;
            case "trino":
                return SQLDialect.TRINO;
            case "yugabytedb":
                return SQLDialect.YUGABYTEDB;

            // enterprise options
            case "db2":
            case "oracle":
            case "sqlserver":
                return SQLDialect.DEFAULT;

            // other
            case "clickhouse":
                return SQLDialect.DEFAULT;
        }

        // catchall
        return SQLDialect.DEFAULT;
    }

    /**
     * jOOQ configuration
     *
     * @param connectionProvider jOOQ connection provider
     * @param dialect            jOOQ SQLDialect
     * @return jOOQ configuration
     */
    @Bean
    public org.jooq.Configuration jooqConfiguration(@Autowired DataSourceConnectionProvider connectionProvider, @Autowired SQLDialect dialect) {
        final DefaultConfiguration config = new DefaultConfiguration();
        config.set(dialect);
        config.set(connectionProvider);
        config.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
        config.set(settings());

        // config.set(new DefaultTransactionProvider(connectionProvider));

        // config.set(new ThreadLocalTransactionProvider(connectionProvider));
        // config.settings()
        //        .withExecuteWithOptimisticLockingExcludeUnversioned(true);
        //
        // PlatformTransactionManager txManager = ...
        // config.set(new SpringTransactionProvider(txManager));

        return config;
    }

    /**
     * jOOQ Settings
     *
     * @return jOOQ settings
     */
    @Bean
    public Settings settings() {
        final Settings settings = new Settings();
        //settings.withRenderSchema(false);
        settings.withRenderFormatted(true);
        return settings;
    }

    /**
     * Get jOOQ connection provider
     *
     * @param dataSource dataSource
     * @return jOOQ connection provider
     */
    @Bean
    public DataSourceConnectionProvider connectionProvider(@Autowired DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    /**
     * Get closeable DSLContext
     *
     * @return jOOQ DSLContext
     */
    @Bean
    public DefaultDSLContext dsl(@Autowired DataSourceConnectionProvider connectionProvider, @Autowired SQLDialect dialect) {
        DefaultDSLContext dsl = new DefaultCloseableDSLContext(connectionProvider, dialect, settings());

        // see https://www.programcreek.com/java-api-examples/?code=curioswitch%2Fcuriostack%2Fcuriostack-master%2Fcommon%2Fserver%2Fframework%2Fsrc%2Fmain%2Fjava%2Forg%2Fcurioswitch%2Fcommon%2Fserver%2Fframework%2Fdatabase%2FDatabaseModule.java
        // Eagerly trigger JOOQ classinit for better startup performance.
        // ---
        // dsl.select().from("curio_server_framework_init").getSQL();

        return dsl;
    }

    /**
     * Class that converts jOOQ exceptions to Spring DataExceptions
     *
     * @return jOOQ exception translator
     */
    @Bean
    public JooqExceptionTranslator exceptionTransformer() {
        return new JooqExceptionTranslator();
    }
}
