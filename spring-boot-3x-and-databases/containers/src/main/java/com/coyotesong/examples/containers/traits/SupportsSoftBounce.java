package com.coyotesong.examples.containers.traits;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import java.io.IOException;
import java.util.Optional;

/**
 * Perform a soft bounce of the server.
 */
public interface SupportsSoftBounce {

    /**
     * Perform a soft bounce of the server.
     * @param container container
     * @return execution results
     * @throws IOException an error occurred communicating with the container
     */
    default Optional<ExecResult> softBounceServer(Container<?> container) throws IOException {
        return Optional.empty();
    }
}
