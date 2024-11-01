/**
 * This package contains Elasticsearch repository interfaces for the Arthub backend application.
 * <p>
 * These repositories handle communication with Elasticsearch, allowing the application
 * to perform complex search operations and index data for faster retrieval.
 * </p>
 */
package org.arthub.backend.Elasticsearch;

import org.arthub.backend.Entity.PostElasticsearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link PostElasticsearch} entities in Elasticsearch.
 * <p>
 * This repository provides custom query methods to search for posts based on their title,
 * description, or post ID in Elasticsearch.
 * </p>
 *
 * @since 1.0
 */
@Repository
public interface PostElasticsearchRepository extends ElasticsearchRepository<PostElasticsearch, String> {

    /**
     * Finds posts by searching for the given keyword in the title or description.
     * <p>
     * This method uses a custom Elasticsearch query to perform a wildcard search
     * on both the title and description fields.
     * </p>
     *
     * @param keyword  the keyword to search for
     * @param pageable pagination details
     * @return a {@link Page} of {@link PostElasticsearch} entities matching the search criteria
     */
    @Query("{ \"bool\": { \"should\": [ "
            + "{ \"wildcard\": { \"title\": \"*?0*\" } }, "
            + "{ \"wildcard\": { \"description\": \"*?0*\" } } "
            + "] } }")
    Page<PostElasticsearch> findByTitleOrDescriptionContaining(String keyword, Pageable pageable);

    /**
     * Finds a post in Elasticsearch by its relational database post ID.
     * <p>
     * This method uses a custom Elasticsearch query to search for a post
     * by its post ID, which links to the relational database post entity.
     * </p>
     *
     * @param postId the ID of the post in the relational database
     * @return an {@link Optional} containing the matching {@link PostElasticsearch}, if found
     */
    @Query("{ \"term\": { \"postId\": ?0 } }")
    Optional<PostElasticsearch> findByPostId(Long postId);
}
