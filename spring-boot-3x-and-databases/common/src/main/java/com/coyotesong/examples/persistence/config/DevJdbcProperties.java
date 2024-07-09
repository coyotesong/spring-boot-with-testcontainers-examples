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

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Configure datasource-related properties (dev profile)
 */
@Configuration
@PropertySource(value = "file:///${user.home}/.config/youtube.properties")
@ConfigurationPropertiesScan
@Profile("dev")
public class DevJdbcProperties {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DevJdbcProperties.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.testQueryString}")
    private String testQueryString;

    /**
     * Datasource properties
     *
     * @return
     */
    @Bean
    public DataSourceProperties dataSourceProperties() {
        final DataSourceProperties props = new DataSourceProperties();
        props.setUrl(url);
        props.setUsername(username);
        props.setPassword(password);
        if (isNotBlank(driverClassName)) {
            props.setDriverClassName(driverClassName);
        }

        return props;
    }

    /**
     * Hikari DataSource
     * <p>
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * @return
     */
    @Bean
    @NotNull
    public HikariDataSource dataSource(@Autowired DataSourceProperties dataSourceProperties) {
        final HikariDataSource ds = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        if (isNotBlank(testQueryString)) {
            ds.setConnectionTestQuery(testQueryString);
        }

        return ds;
    }
}
