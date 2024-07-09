/**
 * TestContainer extensions
 * <p>
 * The test container(s) have been extended so [Flyway](https://flywaydb.org)
 * will initialize the database schema prior to running any additional
 * initialization scripts. I decided this was the best place to trigger flyway
 * or liquibase after a bit of experimentation.
 * <p>
 * The test container(s) will also use the improved LogConsumer.
 * <p>
 * ## Usage
 * <p>
 * The following code snippet must be added to all test classes. At the moment it must
 * be done in the individual classes, not a base class, unless we add a lot of thread-local
 * logic. (Which may be worth it...)
 * <p>
 * ```java
 *
 * @Container
 * @ServiceConnection // @RestartScope
 * static PostgreSQLContainerWithFlyway<?> postgres = new PostgreSQLContainerWithFlyway<>(
 * "postgres:16-alpine" // , resources
 * );
 * <p>
 * DynamicPropertySource
 * static void configureProperties(DynamicPropertyRegistry registry) {
 * registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
 * registry.add("spring.datasource.username", postgres::getUsername);
 * registry.add("spring.datasource.password", postgres::getPassword);
 * registry.add("spring.datasource.driverClassName", postgres::getDriverClassName);
 * registry.add("spring.datasource.testQueryString", postgres::getTestQueryString);
 * }
 * @BeforeAll static void startServer() {
 * if (!postgres.isRunning()) {
 * postgres.start();
 * }
 * }
 * @AfterAll static void shutdownServer() {
 * if (postgres.isRunning()) {
 * postgres.stop();
 * }
 * }
 * ```
 */
package com.coyotesong.examples.containers;