package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.PostElasticsearch;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.PostRepository;
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
     * Repository for managing Post entities in the database.
     */
    private final PostRepository postRepository;

    /**
     * Elasticsearch repository for indexing posts.
     */
    private final PostElasticsearchRepository postElasticsearchRepository;

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
            log.error("Post not found");
            throw new NotFound();
        }
        return post;
    }

    /**
     * Creates a new post and indexes it in Elasticsearch.
     *
     * @param post the post to create
     * @return the created post
     */
    public Post createPost(final Post post) {
        log.info("Post saved to database: {}", post);

        Post newPost = postRepository.save(post);

        PostElasticsearch postElasticsearch = new PostElasticsearch();
        postElasticsearch.setPostId(newPost.getId());
        postElasticsearch.setTitle(post.getTitle());
        postElasticsearch.setDescription(post.getDescription());

        postElasticsearchRepository.save(postElasticsearch);
        return newPost;
    }

    /**
     * Updates an existing post and its corresponding entry in Elasticsearch.
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
            throw new NotFound();
        }

        log.info("Post updated: {}", post);

        postElasticsearch.setTitle(post.getTitle());
        postElasticsearch.setDescription(post.getDescription());

        postElasticsearchRepository.save(postElasticsearch);
        return postRepository.save(post);
    }

    /**
     * Deletes a post by its ID, including its corresponding Elasticsearch entry.
     *
     * @param postId the ID of the post to delete
     * @throws NotFound if the post to delete is not found
     */
    public void deleteById(final Long postId) throws NotFound {
        log.info("Attempting to delete post with ID: {}", postId);

        Post postToDelete = postRepository.findById(postId).orElse(null);
        PostElasticsearch postElasticsearch = postElasticsearchRepository.findByPostId(postId).orElse(null);

        if (postToDelete == null || postElasticsearch == null) {
            log.warn("Post deletion failed: Post or Elasticsearch entry not found for ID: {}", postId);
            throw new NotFound();
        }

        postElasticsearchRepository.delete(postElasticsearch);
        postRepository.delete(postToDelete);

        log.info("Successfully deleted post and its Elasticsearch entry with ID: {}", postId);
    }

    /**
     * Retrieves posts by their IDs.
     *
     * @param postIds a list of IDs of the posts to retrieve
     * @return a list of posts corresponding to the provided IDs
     */
    public List<Post> getPostsById(final List<Long> postIds) {
        List<Post> posts = postRepository.findAllById(postIds);
        log.info("Posts found: {}", posts);
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
        log.info("Folder added to posts: {}", newFolder);
    }

    /**
     * Deletes a folder from the specified list of posts.
     *
     * @param folder the folder to delete from posts
     * @param posts  the list of posts to update
     */
    public void deleteFolderFromPosts(final Folder folder, final List<Post> posts) {
        log.info("Deleting folder: {} from {} posts", folder.getId(), posts.size());

        posts.forEach(post -> {
            if (post.getFolders().remove(folder)) {
                log.info("Folder {} removed from post: {}", folder.getId(), post.getId());
            } else {
                log.warn("Folder {} not found in post: {}", folder.getId(), post.getId());
            }
        });

        postRepository.saveAll(posts);
        log.info("Successfully updated posts after deleting folder: {}", folder.getId());
    }

    /**
     * Deletes a folder from a single post.
     *
     * @param folder the folder to remove from the post
     * @param post   the post from which the folder will be removed
     */
    public void deleteFolderFromPost(final Folder folder, final Post post) {
        log.info("Attempting to delete folder: {} from post: {}", folder.getId(), post.getId());

        if (post.getFolders().remove(folder)) {
            postRepository.save(post);
            log.info("Successfully removed folder {} from post: {}", folder.getId(), post.getId());
        } else {
            log.warn("Folder {} not found in post: {}", folder.getId(), post.getId());
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
        log.info("Tag removed from posts: {}", tag);
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
     * Searches for posts by keyword in Elasticsearch and retrieves them from the database.
     *
     * @param keyword  the search keyword
     * @param pageable pagination details
     * @return a page of posts matching the search criteria
     */
    @Transactional
    public Page<Post> search(final String keyword, final Pageable pageable) {
        log.info("Searching for posts with keyword: {}", keyword);

        Page<PostElasticsearch> posts =
                postElasticsearchRepository.findByTitleOrDescriptionContaining(keyword, pageable);

        if (posts.isEmpty()) {
            log.warn("No posts found for keyword: {}", keyword);
            return Page.empty(pageable);
        }

        List<Long> ids = new ArrayList<>();
        posts.forEach(post -> ids.add(post.getPostId()));

        log.info("Found {} posts in Elasticsearch. Retrieving from the database...", posts.getTotalElements());
        return postRepository.findAllByIds(ids, pageable);
    }

    /**
     * Retrieves posts by their IDs with pagination.
     *
     * @param ids      a list of post IDs
     * @param pageable pagination details
     * @return a page of posts corresponding to the provided IDs
     */
    @Transactional
    public Page<Post> readAllByIds(final List<Long> ids, final Pageable pageable) {
        return postRepository.findAllByIds(ids, pageable);
    }
}
