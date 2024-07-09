# TestContainer autoconfiguration in Spring Boot 3.0

In Spring Boot 3.0 (Spring 6.x) we can use the
[DynamicPropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/DynamicPropertySource.html)
to capture a test container's properties and add them to the ApplicationContext.

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
- contain one or more __static__ methods annotated with `@DynamicPropertySource`.

These properties will be available during configuration.

I have not found a reliable way to use dynamic properties when the annotation is
in a parent class. This hasn't been worth pursuing given the tools added in Spring
Boot 3.1.

```java
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// these annotations will handle starting and stopping the TC instance
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine");
}

/**
 * Capture test container's properties.
 *
 * @param registry dynamic property registry
 */
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driverClassName", postgres::getDriverClassName);
    registry.add("spring.datasource.testQueryString", postgres::getTestQueryString);
}

// tests...
}
```

## Configuration Class Implementation

The configuration class can retrieve these values via the `@Value` annotation. We can use specify
default values with `@value("${key}:defaultValue")`.

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Configure datasource-related properties (test profile)
 */
@Configuration
@Profile("test")
public class TestJdbcProperties {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.testQueryString}")
    private String testQueryString;

    /**
     * Datasource properties
     *
     * @return JDBC configuration properties
     */
    @Bean
    @NotNull
    public DataSourceProperties dataSourceProperties() {
        final DataSourceProperties props = new DataSourceProperties();
        props.setUrl(url);
        props.setUsername(username);
        props.setPassword(password);
        if (isNotBlank(driverClassName)) {
            props.setDriverClassName(driverClassName);
        }

        return props;
    }

    /**
     * Hikari DataSource
     *
     * We specify HikariDataSource, not the more general DataSource, since
     * it is Autocloseable and this simplifies proper connection management.
     *
     * @return configured dataSource
     */
    @Bean
    @NotNull
    public HikariDataSource dataSource() {
        final HikariDataSource ds = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        if (isNotBlank(testQueryString)) {
            ds.setConnectionTestQuery(testQueryString);
        }

        return ds;
    }
}
```
