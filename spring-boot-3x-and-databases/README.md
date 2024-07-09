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

After a bit of experimentation I determined that the best place to handle this
is in the container setup itself. It's possible to handle this in individual
tests, e.g., in a `@BeforeClass` method, but under "don't repeat yourself"
guidance it's better to handle this in a single location
