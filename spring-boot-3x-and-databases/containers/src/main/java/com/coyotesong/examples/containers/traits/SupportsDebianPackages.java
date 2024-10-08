package com.coyotesong.examples.containers.traits;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ExecConfig;

import java.io.IOException;
import java.util.*;

/**
 * Manage Debian packages on container.
 *
 * This action will work on all containers, not just database containers.
 *
 * ## Implementation notes
 *
 * - We can't use absolute paths since some distros use /usr/bin/apt-get, some use /usr/sbin/apt-get, etc.
 *
 * - More complex tasks should be handled by `ansible` playbooks. We can't rely on ansible
 *   being installed on the host OS so we should install it on the container (`apt-get install ansible`),
 *   copy the playbook(s) to the container, and then run ansible locally.
 */
@SuppressWarnings("JavadocBlankLines")
public interface SupportsDebianPackages {
    String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Update package information.
     *
     * This should only need to be called when additional packages will be installed.
     *
     * @param container container
     * @return execution results
     * @throws IOException an error occurred when communicating with the container
     */
    default Optional<ExecResult> updateDebianPackages(Container<?> container) throws IOException {
        final ExecConfig config = ExecConfig.builder()
                .command(new String[] { "apt-get", "update" })
                .user("root")
                .envVars(Collections.singletonMap("DEBIAN_FRONTEND", "noninteractive"))
                .build();

        try {
            return Optional.of(container.execInContainer(config));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }

    /**
     * Upgrade installed packages.
     *
     * This should rarely be necessary - it's usually better to use an updated container.
     *
     * @param container container
     * @return execution results
     * @throws IOException an error occurred when communicating with the container
     */
    default Optional<ExecResult> upgradeDebianPackages(Container<?> container) throws IOException {
        final ExecConfig config = ExecConfig.builder()
                .command(new String[] { "apt-get", "upgrade" })
                .user("root")
                .envVars(Collections.singletonMap("DEBIAN_FRONTEND", "noninteractive"))
                .build();

        try {
            return Optional.of(container.execInContainer(config));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }

    /**
     * Install one or more additional system packages.
     *
     * @param container container
     * @param packageNames list of packages to install
     * @return execution results
     * @throws IOException an error occurred when communicating with the container
     */
    default Optional<ExecResult> installDebianPackages(Container<?> container, String... packageNames) throws IOException {
        if (packageNames.length == 0) {
            return Optional.empty();
        }

        final List<String> cmd = new ArrayList<>();
        cmd.add("apt-get");
        cmd.add("install");
        cmd.add("-y");
        cmd.addAll(Arrays.asList(packageNames));

        final ExecConfig config = ExecConfig.builder()
                .command(cmd.toArray(EMPTY_STRING_ARRAY))
                .user("root")
                .envVars(Collections.singletonMap("DEBIAN_FRONTEND", "noninteractive"))
                .build();

        try {
            return Optional.of(container.execInContainer(config));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }
}
