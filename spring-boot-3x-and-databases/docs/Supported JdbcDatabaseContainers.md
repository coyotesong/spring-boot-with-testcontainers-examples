# Supported JdbcDatabaseContainers

TestContainers officially supports these
[relational databases](https://testcontainers.com/modules/?category=relational-database&language=java).

The following containers currently fail the minimal integration testing. This is almost certainly
due to a simple oversight since I've been focused on the broader issues.

- Oracle Free (`ORA-12514, TNS:listener does not currently know of service requested in connect descriptor`)
- OceanBase CE (`Access denied for user 'root'@'xxx.xxx.xxx.xxx' (using password: NO)`)
- Trino (`No nodes available to run query`)

[Synthesized Test Data Kit (TDK)](https://testcontainers.com/modules/synthesized/) is a tool designed
for database masking and generation of other databases. This looks interesting but is outside of the current scope.

## Relational Databases

| Name        | Description                                                                                                                                                      |
|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DB2         | IBM Db2 is a family of data management products, including database servers.                                                                                     |
| MariaDB     | MariaDB is a community-developed, commercially supported fork of the MySQL relational database management system.                                                |
| MySQL       | MySQL is an open-source relational database management system.                                                                                                   |
| Oracle Free | Oracle Database Free is a free edition of the world’s leading database specifically designed for anybody to develop, learn, and run on Oracle Database for free. |
| Oracle-XE   | Oracle Database Express Edition is a free, smaller-footprint edition of Oracle Database.                                                                         |
| PostGIS     | PostGIS extends the capabilities of the PostgreSQL relational database by adding support for storing, indexing, and querying geospatial data.                    |
| PostreSQL   | PostgreSQL, also known as Postgres, is a free and open-source relational database management system emphasizing extensibility and SQL compliance.                |
| SQL Server  | Microsoft SQL Server is a relational database management system.                                                                                                 |

## Cloud/Distributed Relational Databases

| Name        | Description                                                                                                                                                                                                                                       |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Clickhouse  | ClickHouse is an open-source database management system for analytical processing that allows users to generate reports using SQL queries in real-time.                                                                                           |
| CockRoachDB | CockroachDB is an open-source, cloud-native, resilient, distributed SQL database.                                                                                                                                                                 |
| OceanBase   | OceanBase is an open source unlimited scalable distributed database for data-intensive transactional and real-time operational analytics workloads, with ultra-fast performance that has once achieved world records in the TPC-C benchmark test. |
| TiDB        | TiDB is an open-source NewSQL database that supports Hybrid Transactional and Analytical Processing workloads. It is MySQL compatible and can provide horizontal scalability, strong consistency, and high availability.                          |
| Trino       | Trino is an open-source distributed SQL query engine designed to query large data sets distributed over one or more heterogeneous data sources.                                                                                                   |
| YugabyteDB  | YugabyteDB is a high-performance transactional distributed SQL database for cloud-native applications.                                                                                                                                            |

## Timeseries Databases

| Name      | Description                                                                                                                                                                                                                                                                   |
|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| InfluxDB  | InfluxDB is an open-source time series database for storage and retrieval of time series data in fields such as operations monitoring, application metrics, Internet of Things sensor data, and real-time analytics.                                                          |
| QuestDB   | QuestDB is an open-source time-series database for high throughput ingestion and fast SQL queries with operational simplicity. It supports schema-agnostic ingestion using the InfluxDB line protocol, PostgreSQL wire protocol, and a REST API for bulk imports and exports. |
| Timescale | An open-source time-series SQL database optimized for fast ingest and complex queries. Packaged as a PostgreSQL extension.                                                                                                                                                    |

I have not included InfluxDB since it does not extend `JdbcDatabaseContainer`.

## Docker Containers

The default docker image name is given below. If we use our own extensions we'll typically need to include
a line similar to

```java
dockerImageName.asCompatibleSubstituteFor("postgres");
```

in our test classes.

| TestContainer Name                                              | Docker Container                                                                      | Default Docker Image Name                  | Latest Version |
|-----------------------------------------------------------------|---------------------------------------------------------------------------------------|--------------------------------------------|----------------|
| [ClickHouse](https://testcontainers.com/modules/clickhouse/)    | [clickhouse/clickhouse-server](https://hub.docker.com/r/clickhouse/clickhouse-server) | yandex/clickhouse-server:18.10.3           | 24.6.2.17      |
| [Cockroach DB](https://testcontainers.com/modules/cockroachdb/) | [cockroachdb/cockroach](https://hub.docker.com/r/cockroachdb/cockroach)               | cockroachdb/cockroach:v22.2.3              | 23.2.8         |
| [DB2](https://testcontainers.com/modules/db2/)                  | [ibmcom/db2](https://hub.docker.com/r/ibmcom/db2)                                     | ibmcom/db2:11.5.0.0a                       | 11.5.8.0       |
| [MariaDB](https://testcontainers.com/modules/mariadb/)          | [mariadb](https://hub.docker.com/_/mariadb)                                           | mariadb:10.5.5                             | 11.4.2         |
| [MySQL](https://testcontainers.com/modules/mysql/)              | [mysql](https://hub.docker.com/_/mysql)                                               | mysql:5.7.34                               | 9.0.0          |
| [OceanBase CE](https://testcontainers.com/modules/oceanbase/)   | [oceanbase/oceanbase-ce](https://hub.docker.com/r/oceanbase/oceanbase-ce)             | oceanbase/oceanbase-ce:4.2.2               | 4.3.0          |
| [Oracle Free](https://testcontainers.com/modules/oracle-free/)  | [gvenzl/oracle-free](https://hub.docker.com/r/gvenzl/oracle-free)                     | gvenzl/oracle-free:23.4-slim-faststart     | 23.4           |
| [Oracle XE](https://testcontainers.com/modules/oracle-xe/)      | [glenzl/oracle-xe](https://hub.docker.com/r/gvenzl/oracle-free)                       | gvenzl/oracle-xe:21-slim-faststart         | 21.1.3         |
| [PostGIS](https://testcontainers.com/modules/postgis/)          | [postgis/postgis](https://hub.docker.com/r/postgis/postgis)                           | postgis/postgis:12-3.0                     | 16-3.4         |
| [PostgreSQL](https://testcontainers.com/modules/postgresql/)    | [postgres](https://https://hub.docker.com/_/postgres)                                 | postgres:16-alpine                         | 16.3           |
| [QuestDB](https://testcontainers.com/modules/questdb/)          | [questdb/questdb](https://hub.docker.com/r/questdb/questdb)                           | questdb/questdb:6.5.3                      | 8.0.3          |
| [SQL Server](https://testcontainers.com/modules/mssql/)         | [microsoft/mssql-server](https://hub.docker.com/r/microsoft/mssql-server)             | mcr.microsoft.com/mssql/server:2022-latest | 2202-latest    |
| [TiDB](https://testcontainers.com/modules/tidb/)                | [pingcap/tidb](https://hub.docker.com/r/pingcap/tidb)                                 | pingcap/tidb:v6.1.0                        | v8.2.0         |
| [Timescale](https://testcontainers.com/modules/timescale/)      | [timescale/timescaledb](https://hub.docker.com/r/timescale/timescaledb)               | timescale/timescaledb:2.1.0-pg11           | 2.15.3         |
| [Trino](https://testcontainers.com/modules/trino/)              | [trinodb/trino](https://hub.docker.com/r/trinodb/trino)                               | trinodb/trino:352                          | 452            |
| [YugabyteDB](https://testcontainers.com/modules/yugabytedb/)    | [ygabytedb/yugabyte](https://hub.docker.com/r/yugabytedb/yugabyte)                    | yugabytedb/yugabyte:2.14.4.0-b26           | 2.20.5.0       |

(as of mid-July 2024.)

### Notes

- [DB2](https://testcontainers.com/modules/db2/) is moving from hub.docker.com/r/ibmcom/db2 to icr.io/db2_community/db2.
-
See [Db2 Community Edition for Docker](https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker)
- These are not official Oracle images - if you're concerned by this Oracle allows you to download everything
  you need to build your own images.
- [Presto](https://testcontainers.com/modules/presto/) has been superceded by Trino

## Database Server Details

| Name         | Guest OS         | Server                   | Driver                             | Protocol   | Database | Username             |
|--------------|------------------|--------------------------|------------------------------------|------------|----------|----------------------|
| ClickHouse   | Ubuntu 20.04     | ClickHouse 21.3.20.1     | clickhouse-jdbc 0.3.2              | clickhouse | (none)   | default              |
| Cockroach DB | RHEL 9.4         | PostgreSQL 13.0.0        | (default PostgreSQL JDBC Driver)   | postgresql | postgres | root                 |
| DB2          | RHEL 8.6         | DB2/LINUX SQL110580      | IBM... 4.25.13                     | db2        | (none)   | db2inst1             |
| MariaDB      | Ubuntu 24.04     | MariaDB 11.4.2           | (default MariaDB JDBC driver)      | mariadb    | test     | test                 |
| MySQL        | Oracle Linux 9.4 | MySQL 9.0.0              | (default MySQL Connector/J)        | mysql      | test     | test@172.17.0.1      |
| OceanBase CE | ?                | ?                        | ?                                  | ?          | ?        | root@xxx.xxx.xxx.xxx |
| Oracle Free  | Oracle Linux     | Oracle Database ??       | Oracle JDBC Driver 21.9.0          | oracle     | (none)   | TEST                 |
| Oracle XE    | Oracle Linux 8.9 | Oracle Database 21c (XE) | Oracle JDBC Driver 21.9.0          | oracle     | (none)   | TEST                 |
| PostgreSQL   | Debian 12        | PostgreSQL 16.3          | (default PostgreSQL JDBC Driver)   | postgresql | test     | test                 | 
| QuestDB      | n/a              | PostgreSQL 11.3          | (default PostgreSQL JDBC Driver)   | postgresql | qdb      | admin                |
| SQL Server   | Ubuntu 20.04     | MSSQL 16.00.4065         | (default SQLServer Driver)         | sqlserver  | master   | sa                   |
| TiDB         | Rocky Linux 9.4  | MySQL 8.0.11-TiDB-v8.2.0 | (default MySQL Connector/J)        | mysql      | test     | root@172.17.0.1      |
| Timescale    | n/a              | PostgreSQL 11.11         | (default PostgreSQL JDBC Driver)   | postgresql | test     | test                 |
| Trino        | RHEL 9.4         | Trino 452                | Trino JDBC Driver 452              | trino      | (none)   | test                 |
| YugabyteDB   | AlmaLinux 8.10   | PostgreSQL 11.2          | PostgreSQL JDBC Driver 42.3.5-yb-6 | yugabytedb | yubabyte | yugabyte             |

The default JDBC drivers for MariaDB, MySQL, PostgreSQL, and SQLServer are provided by the
`org.springframework.boot:spring-boot-dependencies` dependency.

Knowing the Guest OS allows us to perform soft bounces, install server extensions, etc.

The extended TestContainers write this information to the log immediately before `runInitScriptsIfRequired()`
is called. An example log entry

```
╔═══════════════╤════════════════════════════════════════════════════════╗
║ property      │ value                                                  ║
╟───────────────┼────────────────────────────────────────────────────────╢
║ docker image  │ postgres:16.3                                          ║
║ guest OS      │ debian "12"                                            ║
║ server        │ PostgreSQL 16.3 (Debian 16.3-1.pgdg120+1)              ║
║ driver        │ PostgreSQL JDBC Driver 42.7.3                          ║
║ jdbc url      │ jdbc:postgresql://localhost:34493/test?loggerLevel=OFF ║
║ database name │ test                                                   ║
║ username      │ test                                                   ║
╚═══════════════╧════════════════════════════════════════════════════════╝
```

Experience has shown that this is incredibly useful information to have when investigating problems
in production since it eliminates any assumptions about the actual database or driver versions.