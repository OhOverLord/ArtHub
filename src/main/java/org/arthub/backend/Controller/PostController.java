/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to post operations.
 * </p>
 */
package org.arthub.backend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.DTO.PostDTO;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Facade.PostFacade;
import org.arthub.backend.Service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing post-related operations.
 * <p>
 * Provides endpoints for retrieving, creating, updating, deleting, and searching posts.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    /**
     * Service for handling basic post-related operations.
     */
    private final PostService service;

    /**
     * Facade for performing complex post-related operations.
     */
    private final PostFacade facade;

    /**
     * Retrieves all posts.
     *
     * @return a {@link List} of all {@link Post} entities
     */
    @GetMapping
    public List<Post> getAllPosts() {
        return service.readAll();
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return {@link ResponseEntity} containing the requested {@link Post}
     * @throws Exception if an error occurs during retrieval
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable final Long postId) throws Exception {
        Post post = service.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * Retrieves posts by user ID (patron ID).
     *
     * @param userId the ID of the user (patron) to retrieve posts for
     * @return {@link ResponseEntity} containing a list of {@link Post} entities belonging to the user
     * @throws Exception if an error occurs during retrieval
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByPatronId(@PathVariable final Long userId) throws Exception {
        List<Post> posts = service.getPostsByPatronId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Creates a new post.
     *
     * @param postDTO the {@link PostDTO} object containing post details
     * @return {@link ResponseEntity} containing the newly created {@link Post}
     * @throws Exception if an error occurs during creation
     */
    @PostMapping
    public ResponseEntity<Post> createPost(
            @Validated(PostDTO.class) @ModelAttribute final PostDTO postDTO
    ) throws Exception {
        Post newPost = facade.createPost(postDTO);
        return ResponseEntity.ok(newPost);
    }

    /**
     * Updates an existing post.
     *
     * @param postId  the ID of the post to update
     * @param postDTO the {@link PostDTO} object containing updated post details
     * @return {@link ResponseEntity} containing the updated {@link Post}
     * @throws Exception if an error occurs during update
     */
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable final Long postId,
            @Validated(PostDTO.class) @ModelAttribute final PostDTO postDTO
    ) throws Exception {
        Post updatedPost = facade.updatePost(postId, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws Exception if an error occurs during deletion
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable final Long postId) throws Exception {
        facade.deletePostById(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves search results based on the given prompt.
     *
     * @param keyword the search query prompt
     * @param pageable pagination details
     * @return ResponseEntity containing a page of posts that match the search prompt
     */
    @PostMapping("/search")
    public ResponseEntity<Page<Post>> getSearchResults(
            @RequestParam final String keyword, final Pageable pageable
    ) {
        Page<Post> posts = service.search(keyword, pageable);
        return ResponseEntity.ok(posts);
    }
}
