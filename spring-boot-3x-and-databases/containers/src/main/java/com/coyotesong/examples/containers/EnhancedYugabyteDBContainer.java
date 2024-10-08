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
import org.testcontainers.containers.YugabyteDBYSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced YugabyteDB test container
 *
 * See [YugabyteDB Module](https://testcontainers.com/modules/yugabytedb/)
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedYugabyteDBContainer extends YugabyteDBYSQLContainer
        implements JdbcDatabaseContainerEnhancements<YugabyteDBYSQLContainer>, ExtendsPostgreSQL<YugayteDBYSQLContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedYugabyteDBContainer.class);

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("yugabytedb/yugabyte:2.14.4.0-b26");
    public static final DockerImageName LATEST_DOCKER_IMAGE_NAME = DockerImageName.parse("yugabytedb/yugabyte:2.20.5.0-b72");

    /**
     * Default constructor
     */
    public EnhancedYugabyteDBContainer() {
        this(LATEST_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name, typically something like "yugabytedb/yugabyte:2.14.4.0-b26"
     */
    @SuppressWarnings("this-escape")
    public EnhancedYugabyteDBContainer(DockerImageName dockerImageName) {
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
