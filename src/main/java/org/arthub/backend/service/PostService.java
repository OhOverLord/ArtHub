package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.*;
import org.arthub.backend.exception.UnauthorizedActionException;
import org.arthub.backend.mapper.PostMapper;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class for managing posts.
 * <p>
 * This class provides methods for creating, updating, deleting, and retrieving posts,
 * as well as managing related entities such as folders and tags. It also integrates
 * with Elasticsearch for faster search operations.
 * </p>
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    /**
     * Repository for accessing and managing Post entities in the database.
     */
    private final PostRepository postRepository;

    /**
     * Repository for accessing and managing Post entities in Elasticsearch.
     */
    private final PostElasticsearchRepository postElasticsearchRepository;
    /**
     * Service for handling operations related to comments.
     */
    private final CommentService commentService;

    /**
     * Retrieves all posts from the database.
     *
     * @return a list of all posts
     */
    public List<Post> readAll() {
        return postRepository.findAll();
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the retrieved post
     * @throws NotFound if the post is not found
     */
    public Post getPostById(final Long postId) throws NotFound {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.error(LogMessages.POST_NOT_FOUND.getMessage(), postId);
            throw new NotFound();
        }
        return post;
    }

    /**
     * Creates a new post and saves it to the database and Elasticsearch.
     *
     * @param post the post to create
     * @return the created post
     */
    public Post createPost(final Post post) {
        log.info(LogMessages.POST_CREATION_ATTEMPT.getMessage(), post);
        Post newPost = postRepository.save(post);
        PostElasticsearch postElasticsearch = PostMapper.toElasticsearchEntity(post);
        postElasticsearchRepository.save(postElasticsearch);
        return newPost;
    }

    /**
     * Updates an existing post.
     *
     * @param postId the ID of the post to update
     * @param post   the updated post
     * @return the updated post
     * @throws NotFound if the post to update is not found
     */
    public Post update(final Long postId, final Post post) throws NotFound {
        Post postToUpdate = postRepository.findById(postId).orElse(null);
        PostElasticsearch postElasticsearch = postElasticsearchRepository.findByPostId(postId).orElse(null);

        if (postToUpdate == null || !Objects.equals(postToUpdate.getId(), post.getId()) || postElasticsearch == null) {
            log.warn(LogMessages.POST_UPDATE_FAILED.getMessage(), postId);
            throw new NotFound();
        }

        log.info(LogMessages.POST_UPDATE_ATTEMPT.getMessage(), post);

        postElasticsearch.setTitle(post.getTitle());
        postElasticsearch.setDescription(post.getDescription());
        postElasticsearch.setTags(PostMapper.mapTags(post.getTags()));

        postElasticsearchRepository.save(postElasticsearch);
        return postRepository.save(post);
    }

    /**
     * Deletes a post by its ID from the database and Elasticsearch.
     *
     * @param postId the ID of the post to delete
     * @throws NotFound if the post to delete is not found
     */
    public void deleteById(final Long postId) throws NotFound {
        log.info(LogMessages.POST_DELETE_ATTEMPT.getMessage(), postId);

        Post postToDelete = postRepository.findById(postId).orElse(null);
        PostElasticsearch postElasticsearch = postElasticsearchRepository.findByPostId(postId).orElse(null);

        if (postToDelete == null || postElasticsearch == null) {
            log.warn(LogMessages.POST_DELETE_FAILED.getMessage(), postId);
            throw new NotFound();
        }

        postElasticsearchRepository.delete(postElasticsearch);
        postRepository.delete(postToDelete);

        log.info(LogMessages.POST_DELETE_SUCCESS.getMessage(), postId);
    }

    /**
     * Retrieves a list of posts by their IDs.
     *
     * @param postIds a list of IDs of the posts to retrieve
     * @return a list of posts corresponding to the provided IDs
     */
    public List<Post> getPostsById(final List<Long> postIds) {
        List<Post> posts = postRepository.findAllById(postIds);
        log.info(LogMessages.POSTS_FOUND.getMessage(), posts);
        return posts;
    }

    /**
     * Adds a folder to multiple posts.
     *
     * @param posts     a list of posts to which the folder will be added
     * @param newFolder the folder to add to the posts
     */
    public void addFolderToPosts(final List<Post> posts, final Folder newFolder) {
        posts.forEach(post -> post.getFolders().add(newFolder));
        postRepository.saveAll(posts);
        log.info(LogMessages.FOLDER_ADDED_TO_POSTS.getMessage(), newFolder);
    }

    /**
     * Deletes a folder from multiple posts.
     *
     * @param folder the folder to remove from the posts
     * @param posts  a list of posts from which the folder will be removed
     */
    public void deleteFolderFromPosts(final Folder folder, final List<Post> posts) {
        log.info(LogMessages.FOLDER_DELETE_FROM_POSTS_ATTEMPT.getMessage(), folder.getId(), posts.size());

        posts.forEach(post -> {
            if (post.getFolders().remove(folder)) {
                log.info(LogMessages.FOLDER_REMOVED_FROM_POST.getMessage(), folder.getId(), post.getId());
            } else {
                log.warn(LogMessages.FOLDER_NOT_FOUND_IN_POST.getMessage(), folder.getId(), post.getId());
            }
        });

        postRepository.saveAll(posts);
        log.info(LogMessages.POSTS_UPDATE_SUCCESS.getMessage(), folder.getId());
    }

    /**
     * Deletes a folder from a single post.
     *
     * @param folder the folder to remove from the post
     * @param post   the post from which the folder will be removed
     */
    public void deleteFolderFromPost(final Folder folder, final Post post) {
        log.info(LogMessages.FOLDER_DELETE_ATTEMPT_FROM_POST.getMessage(), folder.getId(), post.getId());

        if (post.getFolders().remove(folder)) {
            postRepository.save(post);
            log.info(LogMessages.FOLDER_DELETE_SUCCESS_FROM_POST.getMessage(), folder.getId(), post.getId());
        } else {
            log.warn(LogMessages.FOLDER_NOT_FOUND_IN_POST.getMessage(), folder.getId(), post.getId());
        }
    }

    /**
     * Deletes a tag from multiple posts.
     *
     * @param tag   the tag to remove from the posts
     * @param posts a list of posts from which the tag will be removed
     */
    public void deleteTagsFromPost(final Tag tag, final List<Post> posts) {
        posts.forEach(post -> post.getTags().remove(tag));
        postRepository.saveAll(posts);
        log.info(LogMessages.TAG_REMOVED_FROM_POSTS.getMessage(), tag);
    }

    /**
     * Retrieves posts by the patron's ID.
     *
     * @param patronId the ID of the patron whose posts are to be retrieved
     * @return a list of posts belonging to the specified patron
     */
    public List<Post> getPostsByPatronId(final Long patronId) {
        return postRepository.findAllByPatronId(patronId);
    }

    /**
     * Saves a list of posts to the repository.
     *
     * @param userPosts a list of posts to save
     */
    public void saveAll(final List<Post> userPosts) {
        postRepository.saveAll(userPosts);
    }

    /**
     * Searches for posts containing the specified keyword in Elasticsearch and retrieves them from the database.
     *
     * @param keyword  the search keyword
     * @param pageable the pagination information
     * @return a page of posts matching the search keyword
     */
    @Transactional
    public Page<Post> search(final String keyword, final Pageable pageable) {
        log.info(LogMessages.POST_SEARCH_ATTEMPT.getMessage(), keyword);

        Page<PostElasticsearch> posts =
                postElasticsearchRepository.findByTitleOrDescriptionContaining(keyword, pageable);

        if (posts.isEmpty()) {
            log.warn(LogMessages.POST_SEARCH_NO_RESULTS.getMessage(), keyword);
            return Page.empty(pageable);
        }

        List<Long> ids = new ArrayList<>();
        posts.forEach(post -> ids.add(post.getPostId()));

        log.info(LogMessages.POST_SEARCH_RESULTS_FOUND.getMessage(), posts.getTotalElements());
        return postRepository.findAllByIds(ids, pageable);
    }

    /**
     * Deletes a comment from a post if the current user is the author of the comment.
     *
     * @param commentId the ID of the comment to be deleted
     * @param postId the ID of the post from which the comment is to be deleted
     * @param currentUser the {@link User} who is attempting to delete the comment
     * @throws NotFound if the post or comment with the given ID is not found
     * @throws UnauthorizedActionException if the current user is not authorized to delete the comment
     */
    public void deleteCommentFromPost(final Long commentId,
                                      final Long postId,
                                      final User currentUser) throws NotFound, UnauthorizedActionException {
        // Retrieve the post by its ID
        Post post = getPostById(postId);

        if (post == null) {
            log.warn("Post with id {} not found.", postId);
            throw new NotFound();
        }

        // Retrieve the comment by its ID
        Comment comment = commentService.getById(commentId);

        if (comment == null) {
            log.warn("Comment with id {} not found.", commentId);
            throw new NotFound();
        }

        // Check if the current user is the author of the comment
        if (!comment.getAuthorUsername().equals(currentUser.getUsername())) {
            log.warn("User {} is not authorized to delete this comment with id {}",
                    currentUser.getUsername(), commentId);
            throw new UnauthorizedActionException();
        }

        // Attempt to remove the comment from the post
        if (post.getComments().remove(comment)) {
            postRepository.save(post);
            log.info("Comment with id {} successfully deleted from post with id {}",
                    comment.getId(), post.getId());
        } else {
            log.warn("Comment with id {} not found in post with id {}", comment.getId(), post.getId());
        }
    }


    /**
     * Retrieves posts by their IDs with pagination.
     *
     * @param ids      a list of post IDs to retrieve
     * @param pageable the pagination information
     * @return a page of posts with the specified IDs
     */
    @Transactional
    public Page<Post> readAllByIds(final List<Long> ids, final Pageable pageable) {
        return postRepository.findAllByIds(ids, pageable);
    }
}
