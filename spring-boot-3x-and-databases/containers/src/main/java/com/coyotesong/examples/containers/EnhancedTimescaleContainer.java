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

import com.coyotesong.examples.containers.traits.ExtendsPostgreSQL;
import com.coyotesong.examples.containers.traits.JdbcDatabaseContainerEnhancements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced TimescaleDB test container (packaged as PostgreSQL extension)
 *
 * See [Timescale Module]https://testcontainers.com/modules/timescale/)
 *
 * @param <SELF> test container
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedTimescaleContainer<SELF extends PostgreSQLContainer<SELF>> extends PostgreSQLContainer<SELF>
        implements JdbcDatabaseContainerEnhancements<SELF>, ExtendsPostgreSQL<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedTimescaleContainer.class);

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("timescale/timescaledb:2.1.0-pg11");
    public static final DockerImageName LATEST_DOCKER_IMAGE_NAME = DockerImageName.parse("timescale/timescaledb:2.15.3");

    /**
     * Default constructor
     */
    public EnhancedTimescaleContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name, typically something like "timescale/timescaledb:2.1.0-pg11"
     */
    @SuppressWarnings("this-escape")
    public EnhancedTimescaleContainer(DockerImageName dockerImageName) {
        super(dockerImageName.asCompatibleSubstituteFor("postgres"));

        // this block causes 'this-escape'
        resetEnhancements(dockerImageName);
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
