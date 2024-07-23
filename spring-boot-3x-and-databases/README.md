# Spring Boot 3 with Databases

## Project Layout

This project is laid out with:

- a single module ('common') that contains everything but the persistence mechanism(s).
- a single module ('shared-tests') that contains tests expected with all persistence mechanism(s).
- a single module for each persistence mechanism - jooq, spring jpa, spring data, etc.

The latter should, in general, only put code under `com.coyotesong.examples.persistence.{{ artifactId }}`.
The configuration files should specify `@Profile({{ artifactId }})`.

### Shared Tests

The shared tests have two elements:

- classes that contain the actual tests
- classes that link the test, database, and implementation.

This approach could result in combinatorical explosion if we're testing multiple
implementations and multiple database versions. That could be handled with a
framework that performs a Cartesian product of (implementation x database) but
we won't need that for awhile.

### Implementation Note

For unknown reasons the jOOQ tests fail when I add JPA annotations to the classes in the model.
This is unexpected behavior and I'm still investigating it.

## Supported Databases / Test Containers

These test containers extend
[JdbcDatabaseContainer](https://javadoc.io/static/org.testcontainers/jdbc/1.20.0/org/testcontainers/containers/JdbcDatabaseContainer.html)

| Name                                                            | Default Image Name                         | Guest OS         | Latest Version                                                     |
|-----------------------------------------------------------------|--------------------------------------------|------------------|--------------------------------------------------------------------|
| [ClickHouse](https://testcontainers.com/modules/clickhouse/)    | yandex/clickhouse-server:18.10.3           | Ubuntu 20.04     | [24.6.2.17](https://hub.docker.com/r/clickhouse/clickhouse-server) |
| [Cockroach DB](https://testcontainers.com/modules/cockroachdb/) | cockroachdb/cockroach:v22.2.3              | RHEL 9.4         | [v23.2.8](https://hub.docker.com/r/cockroachdb/cockroach)          |
| [DB2](https://testcontainers.com/modules/db2/)                  | ibmcom/db2:11.5.0.0a                       | RHEL 8.6         | [11.5.8.0](https://hub.docker.com/r/ibmcom/db2)                    |
| [MariaDB](https://testcontainers.com/modules/mariadb/)          | mariadb:10.5.5                             | Ubuntu 24.04     | [11.4.2](https://hub.docker.com/_/mariadb)                         |
| [MySQL](https://testcontainers.com/modules/mysql/)              | mysql:5.7.34                               | Oracle Linux 9.4 | [9.0.0](https://hub.docker.com/_/mysql)                            |
| [OceanBase CE](https://testcontainers.com/modules/oceanbase/)   | oceanbase/oceanbase-ce:4.2.2               |                  | [4.3.0](https://hub.docker.com/r/oceanbase/oceanbase-ce)           |
| [Oracle Free](https://testcontainers.com/modules/oracle-free/)  | gvenzl/oracle-free:23.4-slim-faststart     | Oracle Linux     | [23.4](https://hub.docker.com/r/gvenzl/oracle-free)                |
| [Oracle XE](https://testcontainers.com/modules/oracle-xe/)      | gvenzl/oracle-xe:21-slim-faststart         | Oracle Linux 8.9 | [21.1.3](https://hub.docker.com/r/gvenzl/oracle-xe)                |
| [PostGIS](https://testcontainers.com/modules/postgis/)          | postgis/postgis:12-3.0                     | Debian or Alpine | [16-3.4]( https://hub.docker.com/r/postgis/postgis)                |
| [PostgreSQL](https://testcontainers.com/modules/postgresql/)    | postgres:16-alpine                         | Debian or Alpine | [16.3](https://hub.docker.com/_/postgres)                          |
| [QuestDB](https://testcontainers.com/modules/questdb/)          | questdb/questdb:6.5.3                      | ?                | [8.0.3](https://hub.docker.com/r/questdb/questdb)                  |
| [SQL Server](https://testcontainers.com/modules/mssql/)         | mcr.microsoft.com/mssql/server:2022-latest | Ubuntu 20.04     | [2202-latest](https://hub.docker.com/r/microsoft/mssql-server)     |
| [TiDB](https://testcontainers.com/modules/tidb/)                | pingcap/tidb:v6.1.0                        | Rocky Linux 9.4  | [v8.2.0](https://hub.docker.com/r/pingcap/tidb)                    |
| [Timescale](https://testcontainers.com/modules/timescale/)      | timescale/timescaledb:2.1.0-pg11           | ?                | [2.15.3](https://hub.docker.com/r/timescale/timescaledb)           |
| [Trino](https://testcontainers.com/modules/trino/)              | trinodb/trino:352                          | RHEL 9.4         | [452](https://hub.docker.com/r/trinodb/trino)                      |
| [YugabyteDB](https://testcontainers.com/modules/yugabytedb/)    | yugabytedb/yugabyte:2.14.4.0-b26           | AlmaLinux 8.10   | [2.20.5.0](https://hub.docker.com/r/yugabytedb/yugabyte)           |

### Failing Tests

All of the containers execute a 'proof-of-life' functional test. It literally does nothing but start the server
and attempt to log in using the information provided by the container itself. Two test containers fail to
launch (arbitrarily defined as reaching
[JdbcDatabaseContainer#runInitScriptIfRequired()](https://javadoc.io/static/org.testcontainers/jdbc/1.20.0/org/testcontainers/containers/JdbcDatabaseContainer.html#runInitScriptIfRequired--)
and it is not possible to log into the third. These may all be simple oversights - I have not yet dug into
the documentation for each test container

In all cases this is testcontainers 1.20.0 with the most recent docker image.

##### Failure to start

- [Oracle Free](https://testcontainers.com/modules/oracle-free/) - `ORA-12514, TNS:listener does not currently know of service requested in connect descriptor`
- [OceanBase CE](https://testcontainers.com/modules/oceanbase/) - `Access denied for user 'root'@'xxx.xxx.xxx.xxx' (using password: NO)`

##### Failure to connect

- [Trino](https://testcontainers.com/modules/trino/) - `No nodes available to run query`

### Notes

- [Db2 Community Edition for Docker](https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker)
- [DB2](https://testcontainers.com/modules/db2/) is moving from hub.docker.com/r/ibmcom/db2 to icr.io/db2_community/db2.
- These are not official Oracle images - if you're concerned by this Oracle allows you to download everything
  you need to build your own images.
- [Presto](https://testcontainers.com/modules/presto/) has been superceded by Trino
- [Synthesized Test Data Kit (TDK)](https://testcontainers.com/modules/synthesized/) is a tool designed
  for database masking and generation of other databases. This looks interesting but is outside of
  the current scope.
 
### Information Logged on Successful Start of Database

All of the 'enhanced' test containers log standard information when the database is started. (See above -
it's called in an overwritten `runInitScriptIfRequired()` method.) This is the information I've found
invaluable when investigating problems so I always include it a bold text block when I establish the
initial database connection and periodically thereafter.

A representative example is

```
"2024-07-23 00:12:00.830 INFO  [c.c.e.c.EnhancedPostgreSQLContainer:123] - database properties
╔═══════════════╤════════════════════════════════════════════════════════╗
║ property      │ value                                                  ║
╟───────────────┼────────────────────────────────────────────────────────╢
║ docker image  │ postgres:16.3                                          ║
║ guest OS      │ debian "12"                                            ║
║ server        │ PostgreSQL 16.3 (Debian 16.3-1.pgdg120+1)              ║
║ driver        │ PostgreSQL JDBC Driver 42.7.3                          ║
║ jdbc url      │ jdbc:postgresql://localhost:35382/test?loggerLevel=OFF ║
║ database name │ test                                                   ║
║ username      │ test                                                   ║
╚═══════════════╧════════════════════════════════════════════════════════╝
```

### Server Details for Latest Docker Images (July 2024)

This table is prepared using the data mentioned above.

| Name         | Server                    | Driver                                     | Username        |
|--------------|---------------------------|--------------------------------------------|-----------------|
| ClickHouse   | ClickHouse 21.3.20.1      | clickhouse-jdbc 0.3.2                      | default         |
| Cockroach DB | PostgreSQL 13.0           | PostgreSQL JDBC Driver 42.7.3              | root            |
| DB2          | DB2/LINUX SQL110580       | IBM... 4.25.13                             | db2inst1        |
| MariaDB      | MariaDB 11.4.2            | MariaDB Connector/J 3.3.3                  | test            |
| MySQL        | MySQL 9.0.0               | MySQL Connector/J 8.3.0                    | test@172.17.0.1 |
| OceanBase CE | ~failing~                 |                                            |                 |
| Oracle Free  | Oracle Database ??        | Oracle JDBC Driver 21.9.0                  | TEST            |
| Oracle XE    | Oracle Database 21c (XE)  | Oracle JDBC Driver 21.9.0                  | TEST            |
| PostgreSQL   | PostgreSQL 16.3           | PostgreSQL JDBC Driver 42.7.3              | test            |
| QuestDB      | PostgreSQL 11.3           | PostgreSQL JDBC Driver42.7.3               | admin           |
| SQL Server   | MSSQL 16.00.4065          | MSSQL JDBC Driver 12.6 for SQL Server 12.6 | sa              |
| TiDB         | MySQL 8.0.11-TiDB-v8.2.0  | MySQL Connector/J 8.3.0                    | root@172.17.0.1 |
| Timescale    |                           | PostgreSQL JDBC Driver                     | test            |
| Trino        | Trino 452                 | Trino JDBC Driver 452                      | test            |
| YugabyteDB   | PostgreSQL 11.2           | PostgreSQL JDBC Driver 42.3.5-yb-6         | yugabyte        |

## Database Initialization with Flyway or Liquibase

While it's important for each test to set up its own test conditions
they should not be required to set up their own schema, common initialized
data, etc. In fact this is antipattern since it would require identifying
and updating individual tests whenever the database schema changes. It is
far better to handle the schema initialization in a single location even
if it results in the creation of many tables that are irrelevant to the
current tests.

(Flyway)[https://flywaydb.org/] and (Liquibase)[https://www.liquibase.com/]
are two widely used tools to manage database schemas. They can be easily
integrated into our tests and often require nothing more than putting the
required files into a well-known location in our source code.

After a bit of experimentation I decided that the best place to handle this
is in the container setup itself, prior to running any scripts provided by
the user (in `runInitScriptsIfRequired()`.

It is possible to handle this in individual tests, e.g., in a `@BeforeClass`
method, but under "don't repeat yourself" guidance and the need to perform
these tasks before running scripts it seems better to handle this in a
single location, on in the testcontainer itself.
