package org.arthub.backend.StartUpConfig;

/**
 * Class for setting up the environment for the application.
 *
 * This class configures the necessary environment settings
 * based on whether the application is running in Docker or locally.
 */
public class SetUpEnvironment {
    /**
     * Sets up the environment variables required for the application.
     */
    public static void setUp() {
        // set up the environment

        // set up the docker compose
        boolean runningInDocker = Boolean.parseBoolean(System.getenv("RUNNING_IN_DOCKER"));
        if (!runningInDocker) {
            if (!DockerComposeRunner.startDockerCompose()) {
                System.exit(1); // exit if docker compose fails to start
            }
            // for loacl development with database and python server
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
