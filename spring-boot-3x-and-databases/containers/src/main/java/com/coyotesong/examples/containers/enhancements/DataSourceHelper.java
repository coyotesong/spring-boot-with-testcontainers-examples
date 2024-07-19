package com.coyotesong.examples.containers.enhancements;

import com.coyotesong.examples.containers.PostConstructAction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.shaded.com.google.common.base.Splitter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Helper class for DataSources
 */
public class DataSourceHelper<SELF extends JdbcDatabaseContainer<SELF>> {
    private final JdbcDatabaseContainer<SELF> container;

    /**
     * Constructor
     *
     * @param container TestContainer
     */
    public DataSourceHelper(JdbcDatabaseContainer<SELF> container) {
        this.container = container;
    }

    /**
     * Get single-use connection (for initial configuration)
     *
     * @return single use Connection
     * @throws SQLException a problem occurred
     */
    public Connection getConnection() throws SQLException {
        final Properties connInfo = new Properties();
        connInfo.setProperty("user", container.getUsername());
        connInfo.setProperty("password", container.getPassword());
        return HikariConnectionPoolHelper.INSTANCE.connect(container.getJdbcDriverInstance(), container.getJdbcUrl(), connInfo);
    }

    /**
     * Get default poolName for the container
     *
     * @return Hikari pool name
     */
    public String getPoolName() {
        final List<String> components = Splitter.on(':').splitToList(container.getJdbcUrl());
        if (components.size() < 2) {
            return null;
        }
        try {
            return components.get(1) + "_testcontainer." + container.getDatabaseName();
        } catch (UnsupportedOperationException e) {
            // SQLServer
            return components.get(1) + "_testcontainer";
        }
    }

    /**
     * Get Hikari DataSource
     *
     * This is an improvement over a standard DataSource since it's Autocloseable.
     *
     * @return new dataSource
     */
    public HikariDataSource getDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(container.getDriverClassName());
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        // config.setConnectionTestQuery(container.getTestQueryString());
        config.setPoolName(getPoolName());

        return HikariConnectionPoolHelper.INSTANCE.getDataSource(config);
    }

    /**
     * Perform post-construction tasks
     */
    public void postConstruct() {
    }

    /**
     * Perform pre-destruction tasks
     */
    public void preDestroy() {
        HikariConnectionPoolHelper.INSTANCE.close();
    }

    /**
     * Perform the action
     *
     * @param action postconstruct action
     * @return return value
     */
    public <R> R apply(PostConstructAction<JdbcDatabaseContainer<?>, R> action) {
        return action.apply(container);
    }
}
