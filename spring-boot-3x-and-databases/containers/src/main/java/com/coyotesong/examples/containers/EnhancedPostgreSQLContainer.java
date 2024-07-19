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
import com.coyotesong.examples.containers.traits.SupportsServerExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced PostgreSQL test container
 *
 * See [PostgreSQL module](https://testcontainers.com/modules/postgresql/)
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedPostgreSQLContainer<SELF extends PostgreSQLContainer<SELF>> extends PostgreSQLContainer<SELF>
        implements JdbcDatabaseContainerEnhancements<SELF>, SupportsServerExtensions {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedPostgreSQLContainer.class);

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("postgres:16.3");

    /**
     * Default constructor
     */
    public EnhancedPostgreSQLContainer() {
        this(DEFAULT_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name, typically something like "postgres:16.3"
     */
    @SuppressWarnings("this-escape")
    public EnhancedPostgreSQLContainer(DockerImageName dockerImageName) {
        super(dockerImageName);

        resetEnhancements(dockerImageName);

        // this block causes 'this-escape'
        final List<Consumer<OutputFrame>> consumers = new ArrayList<>(super.getLogConsumers());
        consumers.add(logConsumer.get());
        super.setLogConsumers(consumers);
    }

    public EnhancedPostgreSQLContainer<SELF> withActions(Collection<PostConstructAction<SELF, ?>> actions) {
        //this.actions.addAll(actions);
        return this;
    }

    public final EnhancedPostgreSQLContainer<SELF> withAction(Class<? extends PostConstructAction<SELF, ?>> clz) {
        // FIXME
        return this;
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
