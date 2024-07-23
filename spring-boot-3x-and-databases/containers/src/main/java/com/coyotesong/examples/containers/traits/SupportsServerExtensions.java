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

package com.coyotesong.examples.containers.traits;

import com.coyotesong.examples.containers.DatabaseServerExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Convenience interface that adds methods aware of database server extensions
 *
 * This is currently limited to PostgreSQL databases.
 */
@SuppressWarnings({"unused", "SqlSourceToSinkFlow", "JavadocBlankLines"})
public interface SupportsServerExtensions extends JdbcDatabaseAware {

    /**
     * List matching installed extensions
     *
     * @param query database-specific query
     * @return list of installed extensions
     */
    default List<DatabaseServerExtension> listExtensionsByQuery(String query) throws SQLException {
        final List<DatabaseServerExtension> extensions = new ArrayList<>();
        final Properties connInfo = new Properties();
        connInfo.setProperty("user", getUsername());
        connInfo.setProperty("password", getPassword());
        try (Connection conn = getJdbcDriverInstance().connect(getJdbcUrl(), connInfo);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                extensions.add(new DatabaseServerExtension(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4)));
            }
        }

        return extensions;
    }

    /**
     * List matching installed extensions
     *
     * @param name extension name
     * @return list of installed extensions
     */
    default List<DatabaseServerExtension> listInstalledExtensionByName(String name) throws SQLException {
        final List<DatabaseServerExtension> extensions = new ArrayList<>();
        final Properties connInfo = new Properties();
        connInfo.setProperty("user", getUsername());
        connInfo.setProperty("password", getPassword());
        try (Connection conn = getJdbcDriverInstance().connect(getJdbcUrl(), connInfo);
             PreparedStatement ps = conn.prepareStatement(listInstalledExtensionsByNameQuery())) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    extensions.add(new DatabaseServerExtension(rs.getString(1), rs.getString(2),
                            rs.getString(3), rs.getString(4)));
                }
            }
        }

        return extensions;
    }

    /**
     * List all extensions
     *
     * @return list of matching database server extensions
     * @throws SQLException unable to connect to server
     */
    default List<DatabaseServerExtension> listAllExtensions() throws SQLException {
        return listExtensionsByQuery(listAllExtensionsQuery());
    }

    /**
     * List available extensions
     *
     * @return list of available database server extensions
     * @throws SQLException unable to connect to server
     */
    default List<DatabaseServerExtension> listAvailableExtensions() throws SQLException {
        return listExtensionsByQuery(listAvailableExtensionsQuery());
    }

    /**
     * List installed extensions
     *
     * @return list of installed database server extensions
     * @throws SQLException unable to connect to server
     */
    default List<DatabaseServerExtension> listInstalledExtensions() throws SQLException {
        return listExtensionsByQuery(listInstalledExtensionsQuery());
    }

    // default for PostgreSQL
    default String listInstalledExtensionsByNameQuery() {
        return """
                select name, default_version, installed_version, comment
                from pg_catalog.pg_available_extensions
                where installed_version is not null and name = ?
                order by name, default_version, installed_version
                """;
    }

    default String listAllExtensionsQuery() {
        return """
                select name, default_version, installed_version, comment
                from pg_catalog.pg_available_extensions
                order by name, default_version, installed_version
                """;
    }

    default String listAvailableExtensionsQuery() {
        return """
                select name, default_version, installed_version, comment
                from pg_catalog.pg_available_extensions
                order by name, default_version, installed_version
                """;
    }

    default String listInstalledExtensionsQuery() {
        return """
                select name, default_version, installed_version, comment
                from pg_catalog.pg_available_extensions
                where installed_version is not null
                order by name, default_version, installed_version
                """;
    }
}
