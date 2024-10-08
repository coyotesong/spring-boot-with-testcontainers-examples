package com.coyotesong.examples.containers.actions;

import com.coyotesong.examples.containers.PostConstructAction;
import com.coyotesong.examples.containers.traits.SupportsRedhatPackages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;

import java.io.IOException;
import java.util.Optional;

/**
 * Manage Redhat packages on container.
 */
public class UpdateRedhatPackagesAction implements PostConstructAction<Container<?>, Optional<ExecResult>> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateRedhatPackagesAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Update Redhat package information";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ExecResult> apply(Container<?> container) {
        if (container instanceof SupportsRedhatPackages redhat) {
            try {
                final Optional<ExecResult> opt = redhat.updateRedhatPackages(container);
                if (opt.isPresent()) {
                    final ExecResult result = opt.get();
                    if (result.getExitCode() != 0) {
                        LOG.warn(result.getStderr());
                    }
                }

                return opt;
            } catch (IOException e) {
                // handle exception?
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
