package org.arthub.backend.configuration.startup;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class for managing Docker Compose operations.
 * <p>
 * This class provides methods to start Docker Compose
 * and check if a specific Docker Compose service is running.
 */
@Slf4j
public final class DockerComposeRunner {

    // Private constructor to prevent instantiation
    private DockerComposeRunner() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Starts Docker Compose using the specified configuration file.
     *
     * @return true if Docker Compose started successfully; false otherwise
     */
    public static boolean startDockerCompose() {
        try {
            if (!isDockerComposeRunning()) {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("docker-compose", "-f", "docker-compose-local.yml", "up", "-d");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("Docker Compose failed to start");
                    return false;
                } else {
                    log.info("Docker Compose started successfully");
                }
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted during Docker Compose execution: " + e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the Docker Compose service is currently running.
     *
     * @return true if the service is running; false otherwise
     * @throws Exception if an error occurs while checking the service status
     */
    static boolean isDockerComposeRunning() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker-compose", "ps");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("arthub_backend") && line.contains("Up")) {
                log.info("Docker Compose is already running");
                return true;
            }
        }
        return false;
    }
}
