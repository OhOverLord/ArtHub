/**
 * This package contains configuration classes for the Arthub backend application.
 * <p>
 * These configurations include settings for third-party integrations and other
 * environment-specific configurations.
 * </p>
 */
package org.arthub.backend.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Cloudinary.
 * <p>
 * This class provides a Cloudinary bean configured with credentials from the environment variables.
 * It uses Dotenv to load environment variables for cloud name, API key, and API secret.
 * </p>
 *
 * @since 1.0
 */
@Configuration
public class CloudinaryConfig {
    /**
     * Creates and configures a Cloudinary bean with the necessary credentials.
     *
     * @return a configured {@link Cloudinary} instance
     */
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));
    }
}
