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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configure transaction-related properties
 */
@Configuration
@EnableTransactionManagement
public class TransactionProperties {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TransactionProperties.class);

    // @Bean
    // @NotNull
    // public TransactionAwareDataSourceProxy transactionAwareDataSource(@Autowired DataSource dataSource) {
    //    return new TransactionAwareDataSourceProxy(dataSource);
    // }

    @Bean
    @NotNull
    public DataSourceTransactionManager transactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

