/*
 * Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coyotesong.examples.containers.utility;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple metrics tracker
 *
 * This is a minimal metrics tracker intended as POC. See the links below for
 * more realistic use cases.
 *
 * See also:
 *
 * - [PrometheusMetricsTracker](https://github.dev/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/prometheus/PrometheusMetricsTracker.java)
 * - [MicrometerMetricsTracker](https://github.dev/brettwooldridge/HikariCP/blob/dev/src/main/java/com/zaxxer/hikari/metrics/micrometer/MicrometerMetricsTracker.java)
 */
@SuppressWarnings({"JavadocBlankLines", "JavadocLinkAsPlainText"})
public class MyMetricsTracker implements IMetricsTracker {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MyMetricsTracker.class);

    private static final AtomicInteger counter = new AtomicInteger();

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Integer id;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String poolName;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final PoolStats poolStats;

    public MyMetricsTracker(String poolName, PoolStats poolStats) {
        this.id = counter.getAndIncrement();
        this.poolName = poolName;
        this.poolStats = poolStats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionAcquiredNanos(@SuppressWarnings("unused") long elapsedAcquiredNanos) {
        // LOG.info("Metrics: '{}', connection {} acquired: {} ms", poolName, id, elapsedAcquiredNanos / 1000L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionCreatedMillis(@SuppressWarnings("unused") long connectionCreatedMillis) {
        // LOG.info("Metrics: '{}', connection {} created: {} ms", poolName, id, connectionCreatedMillis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionUsageMillis(@SuppressWarnings("unused") long elapsedBorrowedMillis) {
        // LOG.info("Metrics: '{}', connection {} usage: {} ms", poolName, id, elapsedBorrowedMillis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordConnectionTimeout() {
        // LOG.info("Metrics: '{}', connection {} timeout", poolName, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // LOG.info("Metrics: '{}': connection {} closed", poolName, id);
    }
}
