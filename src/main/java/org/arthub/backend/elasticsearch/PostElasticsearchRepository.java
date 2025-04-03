/**
 * This package contains Elasticsearch repository interfaces for the Arthub backend application.
 * <p>
 * These repositories handle communication with Elasticsearch, allowing the application
 * to perform complex search operations and index data for faster retrieval.
 * </p>
 */
package org.arthub.backend.elasticsearch;

import org.arthub.backend.entity.PostElasticsearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link PostElasticsearch} entities in Elasticsearch.
 * <p>
 * This repository provides custom query methods to search for posts based on their title,
 * description, tags, or post ID in Elasticsearch.
 * </p>
 *
 * @since 1.0
 */
@Repository
public interface PostElasticsearchRepository extends ElasticsearchRepository<PostElasticsearch, String> {

    /**
     * Finds posts by searching for the given keyword in the title or description.
     * <p>
     * This method uses a custom Elasticsearch query to perform a match search
     * on both the title and description fields.
     * </p>
     *
     * @param keyword  the keyword to search for
     * @param pageable pagination details
     * @return a {@link Page} of {@link PostElasticsearch} entities matching the search criteria
     */
    @Query("{ \"bool\": { \"should\": [ "
            + "{ \"match\": { \"title\": { \"query\": \"?0\", \"minimum_should_match\": \"75%\" } } }, "
            + "{ \"match\": { \"description\": { \"query\": \"?0\", \"minimum_should_match\": \"75%\" } } } "
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

    /**
     * Finds posts that contain at least one of the specified tags.
     * <p>
     * This method filters posts that match any of the provided tag names.
     * </p>
     *
     * @param tags     the list of tag names to search for
     * @param pageable pagination details
     * @return a {@link Page} of {@link PostElasticsearch} entities matching the specified tags
     */
    @Query("""
    {
      "bool": {
        "filter": [
          {
            "terms": {
              "tags": ?0
            }
          }
        ]
      }
    }
    """)
    Page<PostElasticsearch> findPostsByTags(List<String> tags, Pageable pageable);

    /**
     * Finds posts that do not contain any of the specified tags.
     * <p>
     * This method filters out posts that match any of the provided tag names.
     * </p>
     *
     * @param tags     the list of tag names to exclude
     * @param pageable pagination details
     * @return a {@link Page} of {@link PostElasticsearch} entities excluding the specified tags
     */
    @Query("""
    {
      "bool": {
        "must_not": [
          {
            "terms": {
              "tags": ?0
            }
          }
        ]
      }
    }
    """)
    Page<PostElasticsearch> findPostsExcludingTags(List<String> tags, Pageable pageable);

    /**
     * Finds posts that do not have any tags.
     * <p>
     * This method filters posts that do not have a "tags" field in Elasticsearch.
     * </p>
     *
     * @param pageable pagination details
     * @return a {@link Page} of {@link PostElasticsearch} entities without tags
     */
    @Query("""
    {
      "bool": {
        "must_not": [
          {
            "exists": {
              "field": "tags"
            }
          }
        ]
      }
    }
    """)
    Page<PostElasticsearch> findPostsWithoutTags(Pageable pageable);
}
