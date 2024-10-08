package com.coyotesong.examples.containers.actions;

import com.coyotesong.examples.containers.PostConstructAction;
import com.coyotesong.examples.containers.traits.SupportsDebianPackages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Container.ExecResult;

import java.io.IOException;
import java.util.Optional;

public class UpdateDebianPackagesAction implements PostConstructAction<JdbcDatabaseContainer<?>, Optional<ExecResult>> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateDebianPackagesAction.class);

    /**
     * Get name of the action
     *
     * @return name of action
     */
    @Override
    public String getName() {
        return "Update Debian package information";
    }

    /**
     * {@inheritDoc}
     *
     * @param container
     * @throws IOException an error occurred communicating with the guest OS
     */
    @Override
    public Optional<ExecResult> apply(JdbcDatabaseContainer<?> container) {
        if (container instanceof SupportsDebianPackages debian) {
            try {
                final Optional<ExecResult> opt = debian.updateDebianPackages(container);
                if (opt.isPresent()) {
                    final ExecResult result = opt.get();
                    if (result.getExitCode() != 0) {
                        LOG.warn(result.getStderr());
                    }
                }

                return opt;
            } catch (IOException e) {
                // handle?...
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
