# jOOQ Details

## Building

This module uses the jOOQ code generation plugin. It puts the generated source code into a
separate source directory - `src/main/java.jooq`. Many implementations put the generated
source code in the target directory but that prevents the code from being put under source
control and requires it to be rebuilt by every user. This is undesirable since there's no
guarantee that every developer will have docker installed and properly configured, etc.

__IMPORTANT__: we want to put the flyway files in the `common` directory but at the moment
the flyway plugin appears to be limited to a filesystem location. (I need to verify this -
the information may be outdated.) As a temporary workaround there's a local copy but the
ultimate solution will be to either specify a classpath resource or an explicit flyway
configuration file.

__IMPORTANT__: the tests fail if the stored classes contain JPA annotations, e.g., for
autogeneration of the flyway migration files or for other persistence mechanisms. This is
unexpected behavior and being investigated.

## Usage (Configuration)

The configuration files should be placed in the `com.coyotesong.examples.persistence.config` directory.

#### Beans required for jOOQ

```java
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Configure jooq-related properties (default profile)
 */
@Configuration
// @PropertySource(value = "file:///${user.home}/youtube/youtube.properties")  // for dev profile
@PropertySource(value = "file:///etc/youtube/youtube.properties")
@ConfigurationPropertiesScan
@Profile("default")
public class JooqProperties {
    private static final Logger LOG = LoggerFactory.getLogger(JooqProperties.class);

    @Value("${jooq.sql.dialect:POSTGRES}")
    private String dialect;

    private final DataSourceConnectionProvider connectionProvider;

    public JooqProperties(DataSourceConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * jOOQ configuration
     *
     * @return
     */
    @Bean
    public org.jooq.Configuration jooqConfiguration() {
        final DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.valueOf(dialect));
        jooqConfiguration.set(connectionProvider);
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
        jooqConfiguration.set(settings());

        return jooqConfiguration;
    }

    /**
     * jOOQ Settings
     * @return
     */
    @Bean
    public Settings settings() {
        return new Settings();
    }

    /**
     * Create jOOQ DSLContext
     *
     * @return
     */
    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultCloseableDSLContext(connectionProvider, SQLDialect.valueOf(dialect), settings());
    }

    /**
     * Class that converts jOOQ exceptions to Spring DataExceptions
     * @return
     */
    @Bean
    public JooqExceptionTranslator exceptionTransformer() {
        return new JooqExceptionTranslator();
    }
}
```

