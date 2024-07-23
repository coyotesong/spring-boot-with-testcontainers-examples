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

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is a 'smoke test' intended solely to ensure we can launch a working test container.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestConfiguration.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Disabled("ORA-12514, TNS:listener does not currently know of service requested in connect descriptor")
public class TestContainerOracleFree {

    @Container
    @ServiceConnection
    static EnhancedOracleFreeContainer oracle = new EnhancedOracleFreeContainer();

    private final HikariDataSource dataSource;

    public TestContainerOracleFree(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Run the standard test query
     *
     * @throws SQLException the database could not be reached
     */
    @SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve", "SqlSourceToSinkFlow"})
    @Test
    public void performTestQuery() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(oracle.getTestQueryString())) {
            assertTrue(rs.next(), "failed to get response");
            assertEquals(1, rs.getInt(1), "got unexpected result");
        }
    }
}
