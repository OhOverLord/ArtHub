package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.Comment;
import org.arthub.backend.entity.Post;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.CommentRepository;
import org.arthub.backend.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing comments in the system.
 * Provides operations for creating, deleting, and retrieving comments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    /**
     * Service for managing post-related operations.
     */
    private final PostRepository postRepository;

    /**
     * Repository for CRUD operations on comments.
     */
    private final CommentRepository commentRepository;


    /**
     * Creates a new comment.
     *
     * @param postId the ID of the post to which the comment is being added
     * @param text the content of the comment
     * @param authorUsername the username of the comment's author
     * @return the created {@link Comment} object
     * @throws NotFound if the post with the specified ID does not exist
     */
    @Transactional
    public Comment create(final Long postId, final String text, final String authorUsername) throws NotFound {
        log.info("Creating a new comment for author: {}", authorUsername);

        Post post = postRepository.findById(postId).orElseThrow(NotFound::new);

        Comment newComment = Comment.builder()
                .post(post)
                .text(text)
                .authorUsername(authorUsername)
                .date(LocalDateTime.now())
                .build();

        commentRepository.save(newComment);

        post.getComments().add(newComment);
        postRepository.save(post);

        log.info("Comment created successfully with ID: {}", newComment.getId());
        return newComment;
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment to delete
     */
    public void deleteById(final long id) {
        log.info("Deleting comment with ID: {}", id);

        commentRepository.deleteById(id);

        log.info("Comment with ID: {} deleted successfully", id);
    }

    /**
     * Retrieves all comments for a specific post.
     *
     * @param postId the ID of the post to retrieve comments for
     * @return a list of {@link Comment} objects associated with the specified post
     * @throws NotFound if the post with the given ID does not exist
     */
    public List<Comment> getByPostId(final Long postId) throws NotFound {
        log.info("Retrieving comments for post with ID: {}", postId);

        Post post = postRepository.findById(postId).orElseThrow(NotFound::new);
        List<Comment> comments = post.getComments();

        log.info("Found {} comments for post with ID: {}", comments.size(), postId);
        return comments;
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param id the ID of the comment to retrieve
     * @return the {@link Comment} object associated with the specified ID
     * @throws NotFound if the comment with the given ID does not exist
     */
    public Comment getById(final Long id) throws NotFound {
        log.info("Retrieving comment with ID: {}", id);

        Optional<Comment> comment = commentRepository.findById(id);

        if (!comment.isPresent()) {
            log.warn("Not found comment with ID: {}", id);
            throw new NotFound();
        }

        log.info("Found comment with ID: {}", id);
        return comment.get();
    }

    /**
     * Retrieves all comments in the system.
     *
     * @return a list of all {@link Comment} objects
     */
    public List<Comment> getAll() {
        log.info("Retrieving all comments");

        List<Comment> comments = commentRepository.findAll();

        log.info("Found {} comments in total", comments.size());
        return comments;
    }
}
