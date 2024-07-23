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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Configure datasource-related properties (test profile)
 */
@SuppressWarnings("JavadocBlankLines")
@Configuration
public class TestJdbcProperties {

    /**
     * DataSource properties
     *
     * @return connection details
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

        // 'origin' is always null?
        // if (connectionDetails instanceof Origin origin) {

        return props;
    }

    /**
     * Hikari DataSource
     *
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * @param dataSourceProperties connection details
     * @return dataSource
     */
    @Bean(destroyMethod = "close")
    @NotNull
    public HikariDataSource dataSource(@Autowired DataSourceProperties dataSourceProperties) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());

        if (isNotBlank(dataSourceProperties.getDriverClassName())) {
            config.setDriverClassName(dataSourceProperties.getDriverClassName());
        }

        return new HikariDataSource(config);
        // return HikariConnectionPoolHelper.INSTANCE.getDataSource(config);
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
