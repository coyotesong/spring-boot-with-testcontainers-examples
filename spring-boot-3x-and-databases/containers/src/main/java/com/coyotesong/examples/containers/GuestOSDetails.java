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

import java.util.Properties;

/**
 * Guest details
 *
 * Most Linux distros include a `/etc/os-release` file precisely. We can use it to
 * quickly determine which package manager to use - no need to probe for different
 * binaries.  (`/usr/lib/os-release` is also a valid location.)
 *
 * The remaining Linux distros, plus Windows and Mac guests, will need to
 * be checked in a different manner.
 *
 * @param osRelease              contents of guest's /etc/os-release file
 * @param databaseProductName    database name
 * @param databaseProductVersion database (full) product version
 * @param databaseMajorVersion   database major version
 * @param databaseMinorVersion   database minor version
 * @param driverName             JDBC driver name
 * @param driverVersion          JDBC driver version
 */
@SuppressWarnings({"unused", "JavadocBlankLines"})
public record GuestOSDetails(Properties osRelease,
                             String databaseProductName,
                             String databaseProductVersion,
                             int databaseMajorVersion,
                             int databaseMinorVersion,
                             String driverName,
                             String driverVersion) {

    /**
     * Recognized package format
     *
     * This is a 'best guess' attempt based on the `ID` or `ID_LIKE`
     * property. It does not attempt to handle oddballs like the
     * `pkg` used by QNAP NAS devices.
     */
    public enum Packaging {
        DEBIAN,
        REDHAT,
        ALPINE,
        UNKNOWN
    }

    /**
     * Common property names for `os-release`
     */
    private enum CommonKeys {
        // these are always present, with rare exception of 'VERSION*'
        ID,
        NAME,
        PRETTY_NAME,
        VERSION,
        VERSION_ID,

        // also these are nearly always present
        ID_LIKE,
        LOGO,
        VERSION_CODENAME,

        // documentation
        BUG_REPORT_URL,
        DOCUMENTATION_URL, // rare
        HOME_URL,
        SUPPORT_URL,
        PRIVACY_POLICY_URL, // rare

        // miscellaneous
        ANSI_COLOR,
        BUILD_ID,
        DEFAULT_HOSTNAME,
        PLATFORM_ID,
        SUPPORT_END,
        VARIANT,
        VARIANT_ID,
        VENDOR_NAME,
        VENDOR_URL

        // skipping OS-specific property names
    }

    /**
     * Make guess of guest's package format
     *
     * @return appropriate packaging format
     */
    public Packaging packaging() {
        switch (osRelease.getProperty(CommonKeys.ID.name())) {
            case "debian":
            case "ubuntu":
                return Packaging.DEBIAN;

            case "almalinux":
            case "centos":
            case "fedora":
            case "rhel":
            case "rocky":
                return Packaging.REDHAT;

            case "alpine":
                return Packaging.ALPINE;

            default:
                final String idLike = (String) osRelease.getOrDefault(CommonKeys.ID_LIKE.name(), "");
                if (idLike.contains("debian")) {
                    return Packaging.DEBIAN;
                } else if (idLike.contains("rhel")) {
                    return Packaging.REDHAT;
                }
        }

        return GuestOSDetails.Packaging.UNKNOWN;
    }
}
