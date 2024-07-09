# Shared Tests

This module contains share tests that can be run by all persistence mechanisms.
This allows the persistence mechanisms to be easily compared - performance, ease
of use, etc.

At the moment the tests are minimal while I focus on completing multiple
implementations.

## Building

This module depends on the `tests` packages from both `common` and each
implementation. These packages will be built when `mvn test` is run in
the respective modules.

## Implementations

The test implementations are straightforward. The shared tests should be in
abstract classes in the `com.coyotesong.examples.repository` package. These
classes should never have a dependency, direct or indirect, on a specific
implementation.

The actual tests should be in concrete classes should be located in
subdirectories of this package. These classes do little more than provide
the test container and required annotations.

At the moment each test must define its own container. This is due to
race conditions between test classes. I'm investigating solutions but
this has to be balanced with the benefits of test isolation.

Example:
```java
import com.coyotesong.examples.config.PersistenceTestConfiguration;
import com.coyotesong.examples.containers.PostgreSQLContainerWithFlyway;
import com.coyotesong.examples.repository.I18nLanguageRepository;
import com.coyotesong.examples.repository.I18nLanguageRepositoryTests;
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
 * I18nLanguageRepository tests with PostgreSQL + jOOQ
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {
                PersistenceTestConfiguration.class
        })
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@NestedTestConfiguration(NestedTestConfiguration.EnclosingConfiguration.INHERIT)
@ActiveProfiles({"jooq"})
public class Jooq18nLanguageRepositoryTest extends I18nLanguageRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainerWithFlyway<>(
            PersistenceTestConfiguration.postgresqlDockerImageName.asCanonicalNameString()
    );

    /**
     * Constructor
     *
     * @param testObjectFactory  test object factory
     * @param languageRepository repository implementation to be tested
     */
    @Autowired
    public Jooq18nLanguageRepositoryTest(TestObjectFactory testObjectFactory, I18nLanguageRepository languageRepository) {
        super(testObjectFactory, languageRepository);
    }
}
```