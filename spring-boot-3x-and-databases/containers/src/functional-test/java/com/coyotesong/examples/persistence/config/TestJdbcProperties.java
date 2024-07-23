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

import com.coyotesong.examples.containers.enhancements.HikariConnectionPoolHelper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Configure datasource-related properties (test profile)
 */
@Configuration
public class TestJdbcProperties {

    /**
     * Connection properties
     *
     * @return JDBC connection properties
     */
    @Bean
    @NotNull
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DataSourceProperties dataSourceProperties(@Autowired JdbcConnectionDetails connectionDetails) {
        final DataSourceProperties props = new DataSourceProperties();
        props.setUrl(connectionDetails.getJdbcUrl());
        props.setUsername(connectionDetails.getUsername());
        props.setPassword(connectionDetails.getPassword());
        props.setDriverClassName(connectionDetails.getDriverClassName());

        // for XA transactions
        // connectionDetails.getXaDataSourceClassName();

        // 'origin' is always null?
        // if (connectionDetails instanceof Origin) {

        return props;
    }

    /**
     * Hikari DataSource
     *
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * @param dataSourceProperties JDBC connection properties
     * @return configured dataSource
     */
    @Bean(destroyMethod = "close")
    @NotNull
    public HikariDataSource dataSource(@Autowired DataSourceProperties dataSourceProperties) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());

        if (isNotBlank(dataSourceProperties.getDriverClassName())) {
            config.setDataSourceClassName(dataSourceProperties.getDriverClassName());
        }

        return HikariConnectionPoolHelper.INSTANCE.getDataSource(config);
    }
}
