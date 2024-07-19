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

package com.coyotesong.examples.containers.traits;

import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.Driver;

/**
 * Convenience interface that matches JdbcDatabaseContainer
 */
@SuppressWarnings("unused")
public interface JdbcDatabaseAware {

    /**
     * Return the database name
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Return the name of the JDBC driver to use
     *
     * @return the name of the actual JDBC driver to use
     */
    String getDriverClassName();

    /**
     * Obtain an instance of the correct JDBC driver for this particular database container type
     *
     * @return a JDBC Driver
     * @throws JdbcDatabaseContainer.NoDriverFoundException no driver was found
     */
    Driver getJdbcDriverInstance() throws JdbcDatabaseContainer.NoDriverFoundException;

    /**
     * Return the JDBC URL to connect to the dockerized database
     *
     * @return a JDBC URL that may be used to connect to the dockerized DB
     */
    String getJdbcUrl();

    /**
     * Return the standard password
     *
     * @return the standard password that should be used for connections
     */
    String getPassword();

    /**
     * Return a test query suitable for testing the health of the database
     *
     * @return a test query string suitable for testing that this particular database type is alive
     */
    String getTestQueryString();

    /**
     * Return the standard username
     *
     * @return the standard database username that should be used for connections
     */
    String getUsername();

    /**
     * Stop the dockerized database
     */
    void stop();
}
