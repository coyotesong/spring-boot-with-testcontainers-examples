# TestContainer autoconfiguration in Spring Boot 3.1

In Spring Boot 3.1 (Spring 6.x) we can replace the
[DynamicPropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/DynamicPropertySource.html)
methods with a
[ServiceConnection](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/testcontainers/service/connection/ServiceConnection.html)
annotation. The required connection properties will be quietly captured for most
standard test containers.

Custom test containers can use the same mechanism by implementing their own beans
via the
[ConnectionDetailsFactory](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/autoconfigure/service/connection/ConnectionDetailsFactory.html)
interface.

## Special Spring Considerations

The Spring Database frameworks already include integration tests using an embedded H2 database
(by default). In these cases we'll

- need to disable the default database via the `@AutoConfigureTestDatabase` annotation
- use the standard spring data properties (`spring.datasource.url`, etc.)

We do not need to worry about this if using jOOQ.

## Test Class Implementation

Every test class must:

- be annotated as `@SpringJunitTest`, `@SpringBootTest`, etc.
- contain a __static__ TestContainer instance
- that TestContainer instance must be annotated with `@ServiceConnection`

These properties will be available during configuration.

As before I have not found a reliable way to use dynamic properties when the annotation
is in a parent class. It may be possible, even trivial, but I don't have a working
example yet.

```java
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// these annotations will handle starting and stopping the TC instance
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {
                PersistenceTestConfiguration.class
        })
@Testcontainer
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DatabaseTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine");
}

// tests...
}
```

## Configuration Class Implementation

The configuration class can retrieve these values via a
[ConnectionDetails](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/autoconfigure/service/connection/ConnectionDetails.html)
bean. Different test containers will provide different beans

```java
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configure datasource-related properties (test profile)
 */
@Configuration
@Profile("test")
public class TestJdbcProperties {

    /**
     * Hikari DataSource
     *
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * Note: your IDE may report an error since no JdbcConnectionsDetails
     * bean is defined. It is safe to ignore.
     *
     * @return configured data source
     */
    @Bean
    @NotNull
    public HikariDataSource dataSource(@Autowired JdbcConnectionDetails connectionDetails) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(connectionDetails.getJdbcUrl());
        dataSourceProperties.setUsername(connectionDetails.getUsername());
        dataSourceProperties.setPassword(connectionDetails.getPassword());
        dataSourceProperties.setDriverClassName(connectionDetails.getDriverClassName());

        // for XA transactions
        // connectionDetails.getXaDataSourceClassName();

        final HikariDataSource ds = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        return ds;
    }
}
```
