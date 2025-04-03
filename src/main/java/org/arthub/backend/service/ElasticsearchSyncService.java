/**
 * This package contains service classes for the Arthub backend application.
 * <p>
 * These services handle the business logic and serve as intermediaries between
 * controllers and repositories.
 * </p>
 */
package org.arthub.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.mapper.PostMapper;
import org.arthub.backend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for synchronizing posts between the relational database and Elasticsearch.
 * <p>
 * This service ensures that all posts stored in the relational database are indexed
 * in Elasticsearch for efficient search operations.
 * </p>
 *
 * @since 1.0
 */
@Service
@Slf4j
public class ElasticsearchSyncService {

    /**
     * Repository for managing posts in the relational database.
     */
    private final PostRepository postRepository;

    /**
     * Repository for managing posts in Elasticsearch.
     */
    private final PostElasticsearchRepository elasticsearchRepository;

    /**
     * Constructs an instance of {@link ElasticsearchSyncService}.
     *
     * @param postRepository           the repository for accessing posts in the database
     * @param elasticsearchRepository  the repository for accessing posts in Elasticsearch
     */
    public ElasticsearchSyncService(final PostRepository postRepository,
                                    final PostElasticsearchRepository elasticsearchRepository) {
        this.postRepository = postRepository;
        this.elasticsearchRepository = elasticsearchRepository;
    }

    /**
     * Synchronizes posts from the relational database to Elasticsearch.
     * <p>
     * If Elasticsearch does not contain any posts, this method retrieves all posts
     * from the database, maps them to Elasticsearch-compatible entities using
     * {@link PostMapper}, and saves them in Elasticsearch. If Elasticsearch already
     * contains posts, the synchronization is skipped.
     * </p>
     */
    public void syncPosts() {
        if (elasticsearchRepository.count() == 0) {
            List<Post> posts = postRepository.findAll();
            List<PostElasticsearch> elasticPosts = posts.stream()
                    .map(PostMapper::toElasticsearchEntity)
                    .toList();
            elasticsearchRepository.saveAll(elasticPosts);
            log.warn("Elasticsearch posts saved: {}", elasticsearchRepository.count());
        } else {
            log.info("Elasticsearch posts are already synchronized.");
        }
    }
}
