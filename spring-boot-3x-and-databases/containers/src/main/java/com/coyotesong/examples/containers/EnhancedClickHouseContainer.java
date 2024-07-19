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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced ClickHouse test container
 *
 * See [ClickHouse Module](https://testcontainers.com/modules/clickhouse/)
 */
@Deprecated
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText", "DeprecatedIsStillUsed"})
public class EnhancedClickHouseContainer extends ClickHouseContainer
        implements JdbcDatabaseContainerEnhancements<ClickHouseContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedClickHouseContainer.class);

    private static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("yandex/clickhouse-server:18.10.3");
    private static final DockerImageName LATEST_DOCKER_IMAGE_NAME = DockerImageName.parse("yandex/clickhouse-server:21.3.20.1");

    /**
     * Default constructor
     */
    public EnhancedClickHouseContainer() {
        this(LATEST_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name
     */
    @SuppressWarnings({"this-escape"})
    public EnhancedClickHouseContainer(DockerImageName dockerImageName) {
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
