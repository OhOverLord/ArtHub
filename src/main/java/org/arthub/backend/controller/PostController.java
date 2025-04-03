/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to post operations.
 * </p>
 */
package org.arthub.backend.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.PostDTO;
import org.arthub.backend.entity.Comment;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.EmptyFile;
import org.arthub.backend.exception.Forbidden;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.exception.UnauthorizedActionException;
import org.arthub.backend.facade.PostFacade;
import org.arthub.backend.service.CommentService;
import org.arthub.backend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
@Validated
public class PostController {

    /**
     * Service for handling basic post-related operations.
     */
    private final PostService postService;

    /**
     * Service for handling operations related to comments.
     * Provides methods for creating, retrieving, deleting, and managing comments associated with posts.
     */
    private final CommentService commentService;

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
        return postService.readAll();
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return {@link ResponseEntity} containing the requested {@link Post}
     * @throws NotFound if an error occurs during retrieval
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable final Long postId) throws NotFound {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * Retrieves posts by user ID (patron ID).
     *
     * @param userId the ID of the user (patron) to retrieve posts for
     * @return {@link ResponseEntity} containing a list of {@link Post} entities belonging to the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByPatronId(@PathVariable final Long userId) {
        List<Post> posts = postService.getPostsByPatronId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Creates a new post.
     *
     * @param postDTO the {@link PostDTO} object containing post details
     * @return {@link ResponseEntity} containing the newly created {@link Post}
     * @throws NotFound if an error occurs during creation
     * @throws EmptyFile if an error occurs during creation
     */
    @PostMapping
    public ResponseEntity<Post> createPost(
            @Validated(PostDTO.class) @ModelAttribute final PostDTO postDTO
    ) throws NotFound, IOException, EmptyFile {
        Post newPost = facade.createPost(postDTO);
        return ResponseEntity.ok(newPost);
    }

    /**
     * Updates an existing post.
     *
     * @param postId  the ID of the post to update
     * @param postDTO the {@link PostDTO} object containing updated post details
     * @return {@link ResponseEntity} containing the updated {@link Post}
     * @throws EmptyFile if an error occurs during update
     * @throws NotFound if an error occurs during update
     */
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable final Long postId,
            @Validated(PostDTO.class) @ModelAttribute final PostDTO postDTO
    ) throws EmptyFile, NotFound, IOException {
        Post updatedPost = facade.updatePost(postId, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws NotFound if an error occurs during deletion
     * @throws Forbidden if an error occurs during deletion
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable final Long postId) throws NotFound, Forbidden {
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
        Page<Post> posts = postService.search(keyword, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves all comments for a post by its ID.
     *
     * @param postId the ID of the post to retrieve comments for
     * @return {@link ResponseEntity} containing a list of {@link Comment} entities for the given post
     * @throws NotFound if the post with the given ID is not found
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable final Long postId) throws NotFound {
        List<Comment> result = commentService.getByPostId(postId);
        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new comment for a specific post.
     *
     * @param postId the ID of the post to create the comment for
     * @param text the content of the comment
     * @return {@link ResponseEntity} containing the newly created {@link Comment}
     * @throws NotFound if the post with the given ID is not found
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable final Long postId,
            @RequestParam @NotBlank(message = "Comment text must not be empty") final String text
    ) throws NotFound {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Comment newComment = commentService.create(postId, text, currentUser.getUsername());

        return ResponseEntity.ok(newComment);
    }

    /**
     * Deletes a comment from a post by its ID.
     *
     * @param postId the ID of the post from which to delete the comment
     * @param commentId the ID of the comment to delete
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws NotFound if the comment or post with the given ID is not found
     * @throws UnauthorizedActionException if the user is not authorized to delete the comment
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    @Transactional
    public ResponseEntity<Void> deleteComment(@PathVariable final Long postId,
                                              @PathVariable final Long commentId
                                            ) throws NotFound, UnauthorizedActionException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        postService.deleteCommentFromPost(commentId, postId, currentUser);
        commentService.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
