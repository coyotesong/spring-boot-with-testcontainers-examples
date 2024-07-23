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

package com.coyotesong.examples.containers.enhancements;

import com.coyotesong.examples.containers.GuestOSDetails;
import com.coyotesong.examples.containers.utility.MapToBox;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ExecConfig;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility implementation for GuestOSDetails
 *
 * Possible enhancement: add functional interface to handle getting a
 * database connection, for situations where username/password isn't
 * enough.
 *
 * This is currently limited to Linux guests.
 */
@SuppressWarnings({"unused", "SqlNoDataSourceInspection", "SqlResolve", "JavadocBlankLines"})
public class GuestOSDetailsHelper<SELF extends JdbcDatabaseContainer<SELF>> {

    private final Lock lock = new ReentrantLock();
    private final JdbcDatabaseContainer<SELF> container;

    private GuestOSDetails guestOSDetails;

    /**
     * Constructor
     *
     * @param container test container
     */
    public GuestOSDetailsHelper(JdbcDatabaseContainer<SELF> container) {
        this.container = container;
    }

    /**
     * Command to read the Linux-based Guest OS `/etc/os-release` file.
     *
     * @return contents of os-release file
     */
    public Properties loadGuestOSProperties() throws IOException {
        final ExecConfig catOsRelease = ExecConfig.builder()
                .command(new String[]{"/usr/bin/cat", "/etc/os-release"})
                .build();

        final Properties properties = new Properties();
        if ((catOsRelease != null) && container.isRunning()) {
            try {
                final Container.ExecResult r = container.execInContainer(catOsRelease);
                if (r.getExitCode() == 0) {
                    try (Reader reader = new StringReader(r.getStdout())) {
                        properties.load(reader);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return properties;
    }

    /**
     * Get connection
     *
     * This assumes that username/password is sufficient.
     *
     * @return database connection
     * @throws SQLException unable to connect to the server
     */
    public Connection getConnection() throws SQLException {
        final Properties properties = new Properties();
        properties.setProperty("user", container.getUsername());
        properties.setProperty("password", container.getPassword());
        return container.getJdbcDriverInstance().connect(container.getJdbcUrl(), properties);
    }

    /**
     * Get details about the Linux-based Guest OS and database
     *
     * @return guest OS details
     * @throws SQLException unable to connect to the server
     * @throws IOException  unable to connect to the guest OS
     */
    public GuestOSDetails getGuestDetails() throws IOException, SQLException {
        if (!container.isRunning()) {
            return new GuestOSDetails(new Properties(),
                    null, null, 0, 0, null, null);
        }

        if (guestOSDetails == null) {
            lock.lock();
            try {
                if (guestOSDetails == null) {
                    final Properties osRelease = loadGuestOSProperties();

                    // read database metadata
                    try (Connection conn = getConnection()) {
                        final DatabaseMetaData md = conn.getMetaData();
                        return new GuestOSDetails(osRelease,
                                md.getDatabaseProductName(),
                                md.getDatabaseProductVersion(),
                                md.getDatabaseMajorVersion(),
                                md.getDatabaseMinorVersion(),
                                md.getDriverName(),
                                md.getDriverVersion());
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        return guestOSDetails;
    }

    /**
     * Write Guest OS and Connection details to log
     *
     * This method is named 'timestamp()' since it will typically be run periodically
     * so relatively recent information will be available in all log files.
     *
     * @return formatted text
     */
    public String timestamp() {
        final MapToBox.Builder box = MapToBox.newBuilder(Arrays.asList("property", "value"));
        final InspectContainerResponse containerInfo = container.getContainerInfo();
        try {
            final GuestOSDetails details = getGuestDetails();

            if (containerInfo != null) {
                box.addData("docker image", containerInfo.getConfig().getImage());
            } else {
                box.addData("docker image", "n/a");
            }

            if (details != null) {
                box.addData("guest OS", String.format("%s %s",
                        details.osRelease().getProperty("ID", "n/a"),
                        details.osRelease().getProperty("VERSION_ID", "n/a")));
                if (!details.osRelease().containsKey("ID")) {
                    for (String name : details.osRelease().stringPropertyNames()) {
                        box.addData(name, details.osRelease().getProperty(name));
                    }
                }

                box.addData("server", details.databaseProductName() + " " + details.databaseProductVersion());
                box.addData("driver", details.driverName() + " " + details.driverVersion());
            } else {
                box.addData("guest OS", "n/a");
            }

            if (container.isRunning()) {
                try (Connection conn = getConnection()) {
                    final DatabaseMetaData md = conn.getMetaData();
                    box.addData("jdbc url", md.getURL());
                    box.addData("database name", conn.getCatalog());
                    box.addData("username", md.getUserName());
                } catch (SQLException e) {
                    box.addData(e.getClass().getSimpleName(), e.getMessage());
                }
            }
        } catch (IOException | SQLException e) {
            box.addData(e.getClass().getSimpleName(), e.getMessage());
        }

        return box.toString();
    }
}
