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

package com.coyotesong.examples.config;

import com.coyotesong.examples.persistence.config.TransactionProperties;
import com.coyotesong.examples.repository.TestObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration
 */
@Configuration
@Import({
        TestJdbcProperties.class,
        TransactionProperties.class
})
// this will pick up all implementations
@ComponentScan({
        "com.coyotesong.examples.persistence.config"
})
public class PersistenceTestConfiguration {

    public static final DockerImageName postgresqlDockerImageName =
            //DockerImageName.parse("postgres:15.7-alpine");
            DockerImageName.parse("postgres:16.0");

    @Bean
    public TestObjectFactory testObjectFactory() {
        return new TestObjectFactory();
    }
}
