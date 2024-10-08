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
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.tidb.TiDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced TiDB test container
 *
 * See [TiDB Module](https://testcontainers.com/modules/tidb/)
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedTiDBContainer extends TiDBContainer
        implements JdbcDatabaseContainerEnhancements<TiDBContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedTiDBContainer.class);

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("pingcap/tidb:v6.1.0");
    public static final DockerImageName LATEST_DOCKER_IMAGE_NAME = DockerImageName.parse("pingcap/tidb:v8.2.0");

    /**
     * Default constructor
     */
    public EnhancedTiDBContainer() {
        this(LATEST_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name, typically something like "pingcap/tidb:v6.1.0"
     */
    @SuppressWarnings("this-escape")
    public EnhancedTiDBContainer(DockerImageName dockerImageName) {
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
