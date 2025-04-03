/**
 * This package contains client classes for external service integrations in the Arthub backend application.
 * <p>
 * These clients are responsible for communicating with external APIs and services,
 * providing a clear interface for interaction with third-party systems.
 * </p>
 */
package org.arthub.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Client service for interacting with the Association Service API.
 * <p>
 * This service sends HTTP requests to the external Association Service
 * to retrieve associated tags based on the provided list of tags.
 * </p>
 *
 * @since 1.0
 */
@RequiredArgsConstructor
@Service
public class AssociationServiceClient {

    /**
     * RestTemplate for making HTTP requests.
     */
    private final RestTemplate restTemplate;

    /**
     * Base URL of the Association Service API.
     * <p>
     * This value is loaded from the application properties file using the
     * {@code association-service.base-url} property.
     * </p>
     */
    @Value("${association-service.base-url}")
    private String serviceUrl;

    /**
     * Retrieves a list of associated tags from the Association Service.
     * <p>
     * This method sends a POST request to the Association Service with a list of tags
     * and expects a list of associated tags in the response.
     * </p>
     *
     * @param tags a list of tags to retrieve associations for
     * @return a list of associated tags
     */
    public List<String> getAssociations(final List<String> tags) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(tags, headers);
        ResponseEntity<List<String>> response = restTemplate.exchange(
                serviceUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<String>>() {
                }
        );
        return response.getBody();
    }
}
