package org.arthub.backend.Facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.DTO.PostDTO;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.Forbidden;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Service.ImageService;
import org.arthub.backend.Service.PostService;
import org.arthub.backend.Service.TagService;
import org.arthub.backend.Service.UserService;
import org.arthub.backend.Service.FolderService;
import org.arthub.backend.Service.JwtService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Facade for managing posts in the application.
 * This class handles the business logic related to posts,
 * including creating, updating, and deleting posts, as well as
 * managing associated tags, images, and user interactions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostFacade {
    /** Service for managing post-related operations. */
    private final PostService postService;

    /** Service for managing tag-related operations. */
    private final TagService tagService;

    /** Service for managing user-related operations. */
    private final UserService userService;

    /** Service for managing image-related operations. */
    private final ImageService imageService;

    /** Service for managing folder-related operations. */
    private final FolderService folderService;

    /** Service for managing JWT authentication and user retrieval. */
    private final JwtService jwtService;

    /**
     * Creates a new post based on the provided PostDTO.
     *
     * @param postDTO the data transfer object containing post information
     * @return the created Post
     * @throws Exception if there is an issue during post creation
     */
    @Transactional
    public Post createPost(final PostDTO postDTO) throws Exception {
        User user = jwtService.getUserByToken();

        Post newPost = postFromDTO(postDTO, user);
        userService.addPostToUser(newPost.getPatron(), newPost);
        tagService.addPostToTags(newPost.getTags(), newPost);
        imageService.addPostToImages(newPost.getImage(), newPost);
        log.info("Post created: {}", newPost);
        return newPost;
    }

    /**
     * Converts a PostDTO to a Post entity
     * and sets the user and associated tags and image.
     *
     * @param postDTO the data transfer object containing post information
     * @param user    the user creating the post
     * @return the created Post
     * @throws Exception if there is an issue during the conversion
     */
    public Post postFromDTO(
            final PostDTO postDTO, final User user) throws Exception {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        setUserInPost(post, user);
        setTagsInPost(post, postDTO);
        setImageInPost(post, postDTO);
        log.info("Post created from DTO: {}", post);
        return postService.createPost(post);
    }

    /**
     * Sets the tags in the post based on the provided PostDTO.
     *
     * @param post    the post to update
     * @param postDTO the data transfer object containing tag information
     * @throws Exception if there is an issue during tag retrieval
     */
    private void setTagsInPost(
            final Post post, final PostDTO postDTO) throws Exception {
        if (postDTO.getTagsId() == null || postDTO.getTagsId().isEmpty()) {
            log.info("No tags in post");
            return;
        }
        List<Tag> tags = new ArrayList<>();
        for (Long tagId : postDTO.getTagsId()) {
            tags.add(tagService.getTagById(tagId));
        }
        post.setTags(tags);
        log.info("Tags set in post: {}", post.getTags());
    }

    /**
     * Sets the user in the post.
     *
     * @param post the post to update
     * @param user the user to set as the patron
     * @throws Exception if there is an issue during user setting
     */
    private void setUserInPost(final Post post, final User user) throws Exception {
        post.setPatron(user);
        log.info("User set in post: {}", post.getPatron());
    }

    /**
     * Sets the image in the post based on the provided PostDTO.
     *
     * @param post    the post to update
     * @param postDTO the data transfer object containing image information
     * @throws Exception if there is an issue during image creation
     */
    private void setImageInPost(
            final Post post, final PostDTO postDTO) throws Exception {
        post.setImage(imageService.createImage(postDTO.getFile()));
        log.info("Image set in post: {}", post.getImage());
    }

    /**
     * Updates an existing post based on the provided post ID and PostDTO.
     *
     * @param postId  the ID of the post to update
     * @param postDTO the data transfer
     * object containing updated post information
     * @return the updated Post
     * @throws Exception if there is an issue during post update
     */
    @Transactional
    public Post updatePost(
            final Long postId, final PostDTO postDTO) throws Exception {
        Post updatedPost = postService.getPostById(postId);
        updatedPost.setTitle(postDTO.getTitle());
        updatedPost.setDescription(postDTO.getDescription());
        updateTagsInPost(updatedPost, postDTO);
        updateImageInPost(updatedPost, postDTO);
        return postService.update(postId, updatedPost);
    }

    /**
     * Updates the image of the post based on the provided PostDTO.
     *
     * @param updatedPost the post to update
     * @param postDTO     the data transfer object containing image information
     * @throws Exception if there is an issue during image update
     */
    private void updateImageInPost(
            final Post updatedPost, final PostDTO postDTO) throws Exception {
        // Check if image is the same, can I check array of bytes?
        if (postDTO.getFile() == null) { // if no new image
            return;
        }
        if (updatedPost.getImage() == null) { // if no image in post
            updatedPost.setImage(imageService.createImage(postDTO.getFile()));
        } else if (!Arrays.equals(
                updatedPost.getImage().getData(),
                postDTO.getFile().getBytes()
        )) { // if new image and old image is not null
            imageService.deletePostFromImage(
                    updatedPost.getImage(),
                    updatedPost
            );
            updatedPost.setImage(imageService.createImage(postDTO.getFile()));
        }
    }

    /**
     * Updates the tags in the post based on the provided PostDTO.
     *
     * @param updatedPost the post to update
     * @param postDTO the data transfer object
     * containing updated tag information
     * @throws Exception if there is an issue during tag update
     */
    private void updateTagsInPost(
            final Post updatedPost, final PostDTO postDTO) throws Exception {
        // Check old tags with new one
        List<Tag> newTags = tagService.getTagsByIds(postDTO.getTagsId());
        if (newTags.isEmpty()) {
            // Delete all tags from post
            tagService.deletePostFromTags(updatedPost.getTags(), updatedPost);
            updatedPost.setTags(null);
            return;
        }

        List<Tag> oldTags = new ArrayList<>(updatedPost.getTags());
        log.info("Old tags: {}", oldTags);
        for (Tag tag : oldTags) {
            if (!newTags.contains(tag)) {
                tagService.deletePostFromTag(tag, updatedPost);
                updatedPost.getTags().remove(tag);
            } else {
                newTags.remove(tag);
            }
        }
        updatedPost.getTags().addAll(newTags);
        tagService.addPostToTags(newTags, updatedPost);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @throws Exception if the post is not found
     * or the user is not authorized to delete it
     */
    public void deletePostById(final Long postId) throws Exception {
        User user = jwtService.getUserByToken();

        Post postToDelete = postService.getPostById(postId);

        if (postToDelete == null) {
            throw new NotFound();
        }

        String patronUsername = postToDelete.getPatron().getUsername();

        if (!patronUsername.equals(user.getUsername())) {
            throw new Forbidden();
        }

        folderService.deletePostFromFolders(
                postToDelete.getFolders(),
                postToDelete
        );
        userService.deletePostFromUser(postToDelete.getPatron(), postToDelete);
        tagService.deletePostFromTags(postToDelete.getTags(), postToDelete);
        postService.deleteById(postId);
    }

    /**
     * Deletes tags from a list of user posts.
     *
     * @param userPosts the list of posts to delete tags from
     */
    public void deleteTagsFromPosts(final List<Post> userPosts) {
        if (userPosts == null || userPosts.isEmpty()) {
            log.info("No posts to delete tags");
            return;
        }
        userPosts.forEach(post -> {
            if (post.getTags() == null || post.getTags().isEmpty()) {
                return;
            }
            post.getTags().clear();
            tagService.deletePostFromTags(post.getTags(), post);
        });
        postService.saveAll(userPosts);
    }
}