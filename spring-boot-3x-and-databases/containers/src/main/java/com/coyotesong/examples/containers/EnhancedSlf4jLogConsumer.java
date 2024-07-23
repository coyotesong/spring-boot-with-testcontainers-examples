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

package com.coyotesong.examples.containers;

import org.slf4j.Logger;
import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Improved TestContainer LogConsumer
 *
 * One of the biggest problems we face is identifying the underlying cause
 * of any failed database call. The database server logs usually have this
 * information (although it may be disabled in production due to security
 * concerns) but it can be hard for developers to access those logs.
 *
 * This is where the LogConsumer implementations come in. The 'STDOUT'
 * messages can usually be ignored unless you need to verify that
 * the server has been property initialized. In contrast, the 'STDERR'
 * messages often contains a much more precise error message than we
 * get from the JDBC driver. This can save a lot of time.
 *
 * This LogConsumer also removes a bit of the cruft that's duplicated
 * in our log headers. This makes the messages easier to read.
 *
 * @param <T> Log Consumer
 */
@SuppressWarnings({"unused", "JavadocBlankLines"})
public class EnhancedSlf4jLogConsumer<T extends BaseConsumer<T>> extends BaseConsumer<T> implements Consumer<OutputFrame> {
    private final Logger log;

    private final Pattern ISO_PATTERN = Pattern.compile("^([0-9]{4}-[0-9]{2}-[0-9]{2}[T ][0-9]{2}:[0-9]{2}:[0-9]{2}(.[0-9]+)?( UTC)?) (.+)$");

    private boolean disableStdout = true;

    public EnhancedSlf4jLogConsumer(Logger log) {
        this.log = log;
    }

    /**
     * Set 'disable stdout' flag
     *
     * @param disableStdout disable 'STDOUT' if true
     */
    public void setDisableStdout(boolean disableStdout) {
        this.disableStdout = disableStdout;
    }

    /**
     * Returns whether stdout is disabled
     *
     * @return true if 'STDOUT' is disabled
     */
    public boolean isDisableStdout() {
        return disableStdout;
    }

    /**
     * Remove cruft from the message
     *
     * At this time the only thing we remove is a leading timestamp.
     *
     * @param message raw message received from container
     * @return simplified message
     */
    String removeCruft(String message) {

        // this is a fragment of an internal log.
        // leave the 'FATAL' alone.
        final String pattern1 = " LOG: ";
        if (message.contains(pattern1)) {
            message = message.replaceFirst(pattern1, "");
        }

        // match and remove leading timestamps
        final Matcher isoMatcher = ISO_PATTERN.matcher(message);
        if (isoMatcher.find()) {
            return isoMatcher.group(isoMatcher.groupCount());
        }

        return message;
    }

    /**
     * Accept a message from the container
     *
     * TODO - the 'log.isInfoEnabled()' test isn't working as expected...
     *
     * @param outputFrame the message from the container
     */
    @Override
    public void accept(OutputFrame outputFrame) {
        switch (outputFrame.getType()) {
            case STDOUT:
                if (!disableStdout && log.isInfoEnabled()) {
                    log.info(removeCruft(outputFrame.getUtf8String().trim()));
                }
                break;
            case STDERR:
                log.warn(removeCruft(outputFrame.getUtf8String().trim()));
                break;
            case END:
            default:
                log.info(outputFrame.getUtf8String());
        }
    }
}
