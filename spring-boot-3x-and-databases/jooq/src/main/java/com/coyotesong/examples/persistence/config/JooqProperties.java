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

import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * Configure jooq-related properties (default profile)
 */
@Configuration
@PropertySource("file:///etc/youtube/youtube.properties")
@ConfigurationPropertiesScan
@Profile("default")
public class JooqProperties {
    private static final Logger LOG = LoggerFactory.getLogger(JooqProperties.class);

    @Value("${jooq.sql.dialect:POSTGRES}")
    private String dialect;

    /**
     * jOOQ configuration
     *
     * @return
     */
    @Bean
    public org.jooq.Configuration jooqConfiguration(@Autowired DataSourceConnectionProvider connectionProvider) {
        final DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.valueOf(dialect));
        jooqConfiguration.set(connectionProvider);
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
        jooqConfiguration.set(settings());

        return jooqConfiguration;
    }

    /**
     * jOOQ Settings
     *
     * @return
     */
    @Bean
    public Settings settings() {
        return new Settings();
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider(@Autowired TransactionAwareDataSourceProxy transactionAwareDataSource) {
        return new DataSourceConnectionProvider(transactionAwareDataSource);
    }

    /**
     * Closeable DSLContext
     *
     * @return
     */
    @Bean
    public DefaultDSLContext dsl(@Autowired DataSourceConnectionProvider connectionProvider) {
        return new DefaultCloseableDSLContext(connectionProvider, SQLDialect.valueOf(dialect), settings());
    }

    /**
     * Class that converts jOOQ exceptions to Spring DataExceptions
     *
     * @return
     */
    @Bean
    public JooqExceptionTranslator exceptionTransformer() {
        return new JooqExceptionTranslator();
    }
}

