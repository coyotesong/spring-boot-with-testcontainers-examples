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
import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced DB2 test container
 *
 * See [DB2 Module]https://testcontainers.com/modules/db2/)
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines", "JavadocLinkAsPlainText"})
public class EnhancedDb2Container extends Db2Container implements RequiresLicense<Db2Container>,
        JdbcDatabaseContainerEnhancements<Db2Container> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedDb2Container.class);

    public static final DockerImageName DEFAULT_DOCKER_IMAGE_NAME = DockerImageName.parse("ibmcom/db2:11.5.0.0a");
    public static final DockerImageName LATEST_DOCKER_IMAGE_NAME = DockerImageName.parse("ibmcom/db2:11.5.8.0");

    /**
     * Default constructor
     */
    public EnhancedDb2Container() {
        this(LATEST_DOCKER_IMAGE_NAME);
    }

    /**
     * Constructor
     *
     * @param dockerImageName docker image name, typically something like "ibmcom/db2:11.5.0.0a"
     */
    @SuppressWarnings("this-escape")
    public EnhancedDb2Container(DockerImageName dockerImageName) {
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
    public String getTestQueryString() {
        // return super.getTestQueryString();
        return "SELECT 1 FROM sysibm.sysdummy1";
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
