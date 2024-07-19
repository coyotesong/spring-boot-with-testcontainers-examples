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

import com.coyotesong.examples.containers.traits.JdbcDatabaseContainerEnhancements;
import com.coyotesong.examples.containers.traits.RequiresLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced SQL Server test container
 *
 * See [MSSQL Module](https://testcontainers.com/modules/mssql/)
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedSQLServerContainer<SELF extends MSSQLServerContainer<SELF>> extends MSSQLServerContainer<SELF>
        implements RequiresLicense<SELF>, JdbcDatabaseContainerEnhancements<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedSQLServerContainer.class);

    // see https://hub.docker.com/r/microsoft/mssql-server
    public static final DockerImageName MSSQL_2022_UBUNTU_DOCKER_IMAGE_NAME =
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-preview-ubuntu-22.04");
    public static final DockerImageName MSSQL_2022_DOCKER_IMAGE_NAME =
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest");
    public static final DockerImageName MSSQL_2019_DOCKER_IMAGE_NAME =
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2019-latest");
    public static final DockerImageName MSSQL_2017_DOCKER_IMAGE_NAME =
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-latest");

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = MSSQL_2022_DOCKER_IMAGE_NAME;

    /**
     * Default constructor
     */
    public EnhancedSQLServerContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name
     */
    @SuppressWarnings("this-escape")
    public EnhancedSQLServerContainer(DockerImageName dockerImageName) {
        super(dockerImageName);

        resetEnhancements(dockerImageName);

        // this block causes 'this-escape'
        final List<Consumer<OutputFrame>> consumers = new ArrayList<>(super.getLogConsumers());
        consumers.add(logConsumer.get());
        super.setLogConsumers(consumers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        preDestroy();
        super.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runInitScriptIfRequired() {
        postConstruct(this);
        super.runInitScriptIfRequired();
    }
}
