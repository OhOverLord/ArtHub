/**
 * This package contains client classes for external service integrations in the Arthub backend application.
 * <p>
 * These clients are responsible for communicating with external APIs and services,
 * providing a clear interface for interaction with third-party systems.
 * </p>
 */
package org.arthub.backend.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Mock implementation of the {@link AssociationServiceClient} for local development.
 * <p>
 * This service provides predefined responses for the `getAssociations` method,
 * bypassing actual external API calls. It is enabled only in the "local" profile
 * and is marked as the primary implementation of {@link AssociationServiceClient}.
 * </p>
 *
 * @since 1.0
 */
@Service
@Profile("local")
public class MockAssociationServiceClient extends AssociationServiceClient {

    /**
     * Default constructor for creating the mock service client.
     * <p>
     * Since this is a mock implementation, it does not require a
     * {@link org.arthub.backend.configuration.RestTemplateConfig}.
     * </p>
     */
    public MockAssociationServiceClient() {
        super(null);
    }

    /**
     * Mock implementation of the `getAssociations` method.
     * <p>
     * This method returns a predefined list of associated tags, simulating the
     * behavior of the real Association Service.
     * </p>
     *
     * @param tags a list of tags to retrieve associations for
     * @return a predefined list of associated tags
     */
    @Override
    public List<String> getAssociations(final List<String> tags) {
        return Arrays.asList("car", "sunset", "love");
    }
}
