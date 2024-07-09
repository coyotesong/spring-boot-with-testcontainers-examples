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

package com.coyotesong.examples.repository.jooq;

import com.coyotesong.examples.config.PersistenceTestConfiguration;
import com.coyotesong.examples.containers.PostgreSQLContainerWithFlyway;
import com.coyotesong.examples.repository.I18nRegionRepository;
import com.coyotesong.examples.repository.I18nRegionRepositoryTests;
import com.coyotesong.examples.repository.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.NestedTestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * I18nRegionRepository tests with PostgreSQL + jOOQ
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {
                PersistenceTestConfiguration.class
        })
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@NestedTestConfiguration(NestedTestConfiguration.EnclosingConfiguration.INHERIT)
@ActiveProfiles({"jooq"})
public class Jooq18nRegionRepositoryTest extends I18nRegionRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainerWithFlyway<>(
            PersistenceTestConfiguration.postgresqlDockerImageName.asCanonicalNameString()
    );

    /**
     * Constructor
     *
     * @param testObjectFactory test object factory
     * @param regionRepository  repository implementation to be tested
     */
    @Autowired
    public Jooq18nRegionRepositoryTest(TestObjectFactory testObjectFactory, I18nRegionRepository regionRepository) {
        super(testObjectFactory, regionRepository);
    }
}
