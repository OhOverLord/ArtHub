/**
 * This package contains configuration classes for the Arthub backend application.
 * <p>
 * These classes define beans and configurations that customize the behavior of
 * the application components.
 * </p>
 */
package org.arthub.backend.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating and customizing {@link RestTemplate} instances.
 * <p>
 * This class defines a bean for {@link RestTemplate}, which can be used for making
 * HTTP requests to external services.
 * </p>
 *
 * @since 1.0
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a {@link RestTemplate} bean using the provided {@link RestTemplateBuilder}.
     *
     * @param builder the {@link RestTemplateBuilder} used to configure the {@link RestTemplate}
     * @return a configured {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }
}
