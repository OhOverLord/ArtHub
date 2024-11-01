package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.AlreadyExists;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing tag-related operations.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    /**
     * Repository for managing Tag entities in the database.
     */
    private final TagRepository tagRepository;

    /**
     * Service for handling search operations related to tags.
     */
    private final SearchService searchService;

    /**
     * Retrieves all tags.
     *
     * @return a list of all tags
     */
    public List<Tag> readAll() {
        log.info("Fetching all tags");
        List<Tag> tags = tagRepository.findAll();
        log.info("Successfully fetched tags, total count: {}", tags.size());
        return tags;
    }


    /**
     * Retrieves a tag by ID.
     *
     * @param tagId the ID of the tag to retrieve
     * @return the tag with the specified ID
     * @throws NotFound if the tag is not found
     */
    public Tag getTagById(final Long tagId) throws NotFound {
        log.info("Fetching tag by ID: {}", tagId);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            log.warn("Tag not found with ID: {}", tagId);
            throw new NotFound();
        }
        log.info("Successfully retrieved tag with ID: {}", tagId);
        return tag;
    }


    /**
     * Creates a new tag.
     *
     * @param tag the tag to create
     * @return the created tag
     * @throws AlreadyExists if the tag already exists
     */
    public Tag createTag(final Tag tag) throws AlreadyExists {
        log.info("Attempting to create a new tag: {}", tag.getName());

        if (tagRepository.findByName(tag.getName()).isPresent()) {
            log.warn("Tag creation failed: Tag already exists with name: {}", tag.getName());
            throw new AlreadyExists();
        }

        Tag createdTag = tagRepository.save(tag);
        log.info("Successfully created tag with ID: {}", createdTag.getId());
        return createdTag;
    }


    /**
     * Deletes a tag by ID.
     *
     * @param tagId the ID of the tag to delete
     * @throws NotFound if the tag is not found
     */
    public void deleteById(final Long tagId) throws NotFound {
        log.info("Attempting to delete tag with ID: {}", tagId);

        Tag tagToDelete = tagRepository.findById(tagId).orElse(null);
        if (tagToDelete == null) {
            log.warn("Tag deletion failed: Tag not found with ID: {}", tagId);
            throw new NotFound();
        }

        tagRepository.delete(tagToDelete);
        log.info("Successfully deleted tag with ID: {}", tagId);
    }


    /**
     * Retrieves tags by their IDs.
     *
     * @param tagIds the IDs of the tags to retrieve
     * @return a list of tags with the specified IDs
     */
    public List<Tag> getTagsByIds(final List<Long> tagIds) {
        log.info("Fetching tags by IDs: {}", tagIds);
        List<Tag> tags = tagRepository.findAllById(tagIds);
        log.info("Successfully fetched tags, total count: {}", tags.size());
        return tags;
    }


    /**
     * Associates a post with multiple tags.
     *
     * @param tags   the tags to associate the post with
     * @param newPost the post to associate with the tags
     * @throws NotFound if the tags are null or empty
     */

    public void addPostToTags(final List<Tag> tags, final Post newPost) throws NotFound {
        if (tags == null || tags.isEmpty() || tags.contains(null)) {
            log.error("Tags not found or null");
            return;
        }

        for (Tag tag : tags) {
            tag.getPosts().add(newPost);
            log.info("Post added to tags: {}", newPost);
        }
        tagRepository.saveAll(tags);
    }

    /**
     * Removes a post from multiple tags.
     *
     * @param tags the tags to remove the post from
     * @param post the post to disassociate from the tags
     */
    public void deletePostFromTags(final List<Tag> tags, final Post post) {
        log.info("Remove post from Tags");
        for (Tag tag : tags) {
            tag.getPosts().remove(post);
        }
        tagRepository.saveAll(tags);
    }

    /**
     * Removes a post from a specific tag.
     *
     * @param tag  the tag to remove the post from
     * @param post the post to disassociate from the tag
     */
    public void deletePostFromTag(final Tag tag, final Post post) {
        log.info("Remove post from Tag: {}", tag.getName());
        tag.getPosts().remove(post);
        tagRepository.save(tag);
    }

    /**
     * Creates new tags.
     *
     * @param tags the list of tags to create
     * @return the list of created tags
     */
    public List<Tag> createTags(final List<Tag> tags) {
        log.info("Attempting to create multiple tags. Total tags to process: {}", tags.size());
        List<Tag> processedTags = new ArrayList<>();

        for (Tag tag : tags) {
            String tagName = tag.getName().toLowerCase();
            log.info("Processing tag: {}", tagName);
            List<String> processedKeywords = searchService.processPrompt(tagName);

            for (String keyword : processedKeywords) {
                Tag newTag = new Tag();
                newTag.setName(keyword);
                if (tagRepository.findByName(keyword).isEmpty()) {
                    processedTags.add(newTag);
                    log.info("New tag prepared for creation: {}", keyword);
                } else {
                    log.warn("Tag already exists, skipping: {}", keyword);
                }
            }
        }

        List<Tag> createdTags = tagRepository.saveAll(processedTags);
        log.info("Successfully created tags, total created: {}", createdTags.size());
        return createdTags;
    }


    /**
     * Deletes all posts associated with the specified list of user posts.
     *
     * @param userPosts the list of user posts to delete
     */
    public void deletePostsFromTags(final List<Post> userPosts) {

    }

    /**
     * Removes a user from the specified list of preferred tags.
     *
     * @param preferredTags the list of tags to remove the user from
     * @param userToDelete the user to remove from the tags
     */
    public void deleteUserFromTags(final List<Tag> preferredTags, final User userToDelete) {
        log.info("Delete user from tags: {}", preferredTags);
        if (preferredTags == null || preferredTags.isEmpty()) {
            log.error("Tags not found or null");
            return;
        }
        for (Tag tag : preferredTags) {
            if (tag == null) {
                continue;
            }
            tag.getUsers().remove(userToDelete);
        }
        tagRepository.saveAll(preferredTags);
    }

    /**
     * Associates a user with multiple tags.
     *
     * @param tags the tags to associate the user with
     * @param user the user to associate with the tags
     * @throws NotFound if the tags are null or empty
     */
    public void addUserToTags(final List<Tag> tags, final User user) {
        if (tags == null || tags.isEmpty() || tags.contains(null)) {
            log.error("Tags not found or null");
            return;
        }

        for (Tag tag : tags) {
            tag.getUsers().add(user);
            log.info("User added to tags: {}", user);
        }
        tagRepository.saveAll(tags);
    }
}
