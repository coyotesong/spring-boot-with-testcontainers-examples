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

package com.coyotesong.examples.config;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.origin.Origin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Configure datasource-related properties (dev profile)
 */
@Configuration
@ConfigurationPropertiesScan
@Profile("test")
public class TestJdbcProperties {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TestJdbcProperties.class);

    /**
     * Connection properties
     *
     * @return JDBC connection properties
     */
    @Bean
    @NotNull
    public DataSourceProperties dataSourceProperties(@Autowired JdbcConnectionDetails connectionDetails) {
        final DataSourceProperties props = new DataSourceProperties();
        props.setUrl(connectionDetails.getJdbcUrl());
        props.setUsername(connectionDetails.getUsername());
        props.setPassword(connectionDetails.getPassword());
        props.setDriverClassName(connectionDetails.getDriverClassName());

        // for XA transactions
        // connectionDetails.getXaDataSourceClassName();

        if (connectionDetails instanceof Origin origin) {
            LOG.info("***** origin: {}", origin);
        } else {
            LOG.info("***** no origin!");
        }

        return props;
    }

    /**
     * Hikari DataSource
     * <p>
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * @param dataSourceProperties JDBC connection properties
     * @return configured dataSource
     */
    @Bean
    @NotNull
    public HikariDataSource dataSource(@Autowired DataSourceProperties dataSourceProperties) {
        final HikariDataSource ds = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // if (isNotBlank(testQueryString)) {
        //     ds.setConnectionTestQuery(testQueryString);
        // }

        return ds;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Autowired DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(
        //        new ClassPathResource(env.getRequiredProperty("db.schema.script"))
        //);

        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
