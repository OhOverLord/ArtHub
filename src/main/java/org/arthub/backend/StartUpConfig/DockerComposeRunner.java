package org.arthub.backend.StartUpConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class for managing Docker Compose operations.
 *
 * This class provides methods to start Docker Compose
 * and check if a specific Docker Compose service is running.
 */
public class DockerComposeRunner {

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
                    throw new Exception("Docker Compose failed to start");
                } else {
                    System.out.println("Docker Compose started successfully");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
                System.out.println("Docker Compose is already running");
                return true;
            }
        }
        return false;
    }
}
