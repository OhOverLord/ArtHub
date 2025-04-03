package org.arthub.backend.configuration.startup;

/**
 * Class for setting up the environment for the application.
 * <p>
 * This class configures the necessary environment settings
 * based on whether the application is running in Docker or locally.
 */
public final class SetUpEnvironment {

    // Private constructor to prevent instantiation
    private SetUpEnvironment() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Sets up the environment variables required for the application.
     */
    public static void setUp() {
        boolean runningInDocker = Boolean.parseBoolean(System.getenv("RUNNING_IN_DOCKER"));
        if (!runningInDocker) {
            System.setProperty("spring.profiles.active", "local");
            if (!DockerComposeRunner.startDockerCompose()) {
                System.exit(1);
            }
            System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/arthub_backend");
            System.setProperty("spring.elasticsearch.uris", "http://localhost:9200");
        } else {
            System.setProperty("spring.elasticsearch.uris", "http://elastic-service:9200");
            System.setProperty(
                    "spring.datasource.url", "jdbc:postgresql://arthub_backend_database:5432/arthub_backend"
            );
        }
    }
}
