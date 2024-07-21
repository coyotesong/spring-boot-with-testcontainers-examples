/**
 * TestContainer extensions
 *
 * The test container(s) have been extended so [Flyway](https://flywaydb.org)
 * will initialize the database schema prior to running any additional
 * initialization scripts. I decided this was the best place to trigger flyway
 * or liquibase after a bit of experimentation.
 *
 * The test container(s) will also use the improved LogConsumer.
 *
 * ## Usage
 *
 * The following code snippet must be added to all test classes. At the moment it must
 * be done in the individual classes, not a base class, unless we add a lot of thread-local
 * logic. (Which may be worth it...)
 *
 * ```java
 * {@literal @}Container
 * {@literal @}ServiceConnection // {@literal @}RestartScope
 * static PostgreSQLContainerWithFlyway{@literal <?>} postgres = new PostgreSQLContainerWithFlyway{@literal <>}("postgres:16-alpine");
 *
 * {@literal @}DynamicPropertySource
 * static void configureProperties(DynamicPropertyRegistry registry) {
 *     registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
 *     registry.add("spring.datasource.username", postgres::getUsername);
 *     registry.add("spring.datasource.password", postgres::getPassword);
 *     registry.add("spring.datasource.driverClassName", postgres::getDriverClassName);
 *     registry.add("spring.datasource.testQueryString", postgres::getTestQueryString);
 * }
 *
 * {@literal @}BeforeAll
 * static void startServer() {
 *     if (!postgres.isRunning()) {
 *         postgres.start();
 *     }
 * }
 *
 * {@literal @}AfterAll
 * static void shutdownServer() {
 *     if (postgres.isRunning()) {
 *         postgres.stop();
 *     }
 * }
 * ```
 */
@SuppressWarnings({ "JavadocBlankLines", "JavadocLinkAsPlainText" })
package com.coyotesong.examples.containers;