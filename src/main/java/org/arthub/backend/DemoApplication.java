package org.arthub.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class for starting the Arthub backend application.
 * <p>
 * This is the entry point of the Spring Boot application. It sets up the Docker environment and
 * starts the application.
 * </p>
 *
 * @since 1.0
 */
@SpringBootApplication(exclude = {
        JpaRepositoriesAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class
})
@EnableJpaRepositories(basePackages = { "org.arthub.backend.Repository" })
@EnableElasticsearchRepositories(basePackages = {"org.arthub.backend.Elasticsearch"})
public class DemoApplication {
    /**
     * The main method which serves as the entry point for the Spring Boot application.
     * <p>
     * This method first sets up the Docker environment for local development using the
     * </p>
     *
     * @param args command-line arguments passed to the application
     * @throws Exception if an error occurs during environment setup
     */
    public static void main(final String[] args) throws Exception {
        // Set up Docker Compose environment for local development
        SpringApplication.run(DemoApplication.class, args);
    }
}