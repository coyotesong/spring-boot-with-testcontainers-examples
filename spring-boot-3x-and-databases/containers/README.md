# Containers

This module contains an enhanced version of nearly all of the TestContainer databases extending
[JdbcDatabaseContainer](https://javadoc.io/static/org.testcontainers/jdbc/1.19.8/org/testcontainers/containers/JdbcDatabaseContainer.html)

The extensions are implemented with a 'Convention over Configuration' approach using
a handful of interfaces with default implementations.

## PostConstruct and PreDestroy Actions

In time the containers will include support for the execution of arbitrary actions upon
construction (immediately before
[runInitScriptsIfRequired()](https://javadoc.io/static/org.testcontainers/jdbc/1.19.8/org/testcontainers/containers/JdbcDatabaseContainer.html#runInitScriptIfRequired--))
or destruction (immediately before `stop()`.)

This can be used to perform tasks like:

- using Flyway or Liquibase to initialize the database schema