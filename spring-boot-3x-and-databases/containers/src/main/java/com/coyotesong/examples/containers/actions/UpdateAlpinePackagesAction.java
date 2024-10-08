package com.coyotesong.examples.containers.actions;

import com.coyotesong.examples.containers.PostConstructAction;
import com.coyotesong.examples.containers.traits.SupportsAlpinePackages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import java.io.IOException;
import java.util.Optional;

/**
 * Manage Alpine packages on container.
 */
public class UpdateAlpinePackagesAction implements PostConstructAction<Container<?>, Optional<ExecResult>> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateAlpinePackagesAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Update Alpine package information";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ExecResult> apply(Container<?> container) {
        if (container instanceof SupportsAlpinePackages alpine) {
            try {
                final Optional<ExecResult> opt = alpine.updateAlpinePackages(container);
                if (opt.isPresent()) {
                    final ExecResult result = opt.get();
                    if (result.getExitCode() != 0) {
                        LOG.warn(result.getStderr());
                    }
                }
                return opt;
            } catch (IOException e) {
                // optional.throws(?) - or is that only Future<> ?
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
