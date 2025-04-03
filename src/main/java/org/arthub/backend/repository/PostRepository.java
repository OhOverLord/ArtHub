/**
 * This package contains repository interfaces for the Arthub backend application.
 * <p>
 * These repositories handle the communication with the database, allowing the application
 * to retrieve, create, update, and delete entities. It also provides custom queries for
 * complex data access patterns.
 * </p>
 */
package org.arthub.backend.repository;

import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository interface for accessing post-related data in the database.
 * <p>
 * This interface provides methods to perform CRUD operations and custom queries
 * for retrieving posts based on tags, keywords, and patron-specific filters.
 * </p>
 *
 * @since 1.0
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Retrieves all posts with pagination.
     *
     * @param pageable pagination information
     * @return a {@link Page} of {@link Post} entities
     */
    Page<Post> findAll(Pageable pageable);

    /**
     * Retrieves posts by tags with pagination.
     *
     * @param tags     a list of {@link Tag} entities to filter by
     * @param pageable pagination information
     * @return a {@link Page} of {@link Post} entities filtered by tags
     */
    Page<Post> findByTagsIn(List<Tag> tags, Pageable pageable);

    /**
     * Retrieves random posts that do not contain specified tags, with pagination.
     * <p>
     * This method uses a custom query to exclude posts that contain the specified tags.
     * </p>
     *
     * @param pageable pagination information
     * @param tags     a list of {@link Tag} entities to exclude from results
     * @return a {@link Page} of random {@link Post} entities not containing the specified tags
     */
    @Query("SELECT p FROM Post p where p not in "
            + "( select p from Post p join p.tags t where t in (:tags) )")
    Page<Post> findRandom(Pageable pageable, List<Tag> tags);

    /**
     * Searches for posts by keywords in tags and returns distinct results.
     * <p>
     * This method performs a case-insensitive search for posts where the tag names
     * match the given keywords.
     * </p>
     *
     * @param keywords a list of keywords to search for
     * @return a {@link List} of {@link Post} entities matching the search keywords
     */
    @Query("select distinct p from Post p join "
            + "p.tags t where lower(t.name) in (:keywords)")
    List<Post> searchByKeywords(List<String> keywords);

    /**
     * Retrieves all posts by a specific patron.
     * <p>
     * This method retrieves all posts created by a patron with the specified ID.
     * </p>
     *
     * @param patronId the ID of the patron
     * @return a {@link List} of {@link Post} entities created by the specified patron
     */
    @Query("select distinct p from Post p join "
            + "p.patron u where u.id = :patronId")
    List<Post> findAllByPatronId(Long patronId);

    /**
     * Retrieves posts by their IDs with pagination.
     * <p>
     * This method retrieves posts where the post IDs match the specified list.
     * </p>
     *
     * @param ids      a list of post IDs to retrieve
     * @param pageable pagination information
     * @return a {@link Page} of {@link Post} entities matching the given IDs
     */
    @Query("select p from Post p where p.id in :ids")
    Page<Post> findAllByIds(List<Long> ids, Pageable pageable);
}
