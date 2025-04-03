package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.TagRepository;
import org.springframework.stereotype.Service;

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
     * The repository for managing tag entities.
     */
    private final TagRepository tagRepository;

    /**
     * Retrieves all tags.
     *
     * @return a list of all tags
     */
    public List<Tag> readAll() {
        log.info(LogMessages.TAGS_FETCH_ALL.getMessage());
        List<Tag> tags = tagRepository.findAll();
        log.info(LogMessages.TAGS_FETCH_SUCCESS.getMessage(), tags.size());
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
        log.info(LogMessages.TAG_FETCH_BY_ID_ATTEMPT.getMessage(), tagId);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            log.warn(LogMessages.TAG_NOT_FOUND.getMessage(), tagId);
            throw new NotFound();
        }
        log.info(LogMessages.TAG_FETCH_BY_ID_SUCCESS.getMessage(), tagId);
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
        log.info(LogMessages.TAG_CREATION_ATTEMPT.getMessage(), tag.getName());

        if (tagRepository.findByName(tag.getName()).isPresent()) {
            log.warn(LogMessages.TAG_CREATION_FAILED_EXISTS.getMessage(), tag.getName());
            throw new AlreadyExists();
        }

        Tag createdTag = tagRepository.save(tag);
        log.info(LogMessages.TAG_CREATION_SUCCESS.getMessage(), createdTag.getId());
        return createdTag;
    }

    /**
     * Deletes a tag by ID.
     *
     * @param tagId the ID of the tag to delete
     * @throws NotFound if the tag is not found
     */
    public void deleteById(final Long tagId) throws NotFound {
        log.info(LogMessages.TAG_DELETE_ATTEMPT.getMessage(), tagId);

        Tag tagToDelete = tagRepository.findById(tagId).orElse(null);
        if (tagToDelete == null) {
            log.warn(LogMessages.TAG_DELETE_FAILED_NOT_FOUND.getMessage(), tagId);
            throw new NotFound();
        }

        tagRepository.delete(tagToDelete);
        log.info(LogMessages.TAG_DELETE_SUCCESS.getMessage(), tagId);
    }

    /**
     * Retrieves tags by their IDs.
     *
     * @param tagIds the IDs of the tags to retrieve
     * @return a list of tags with the specified IDs
     */
    public List<Tag> getTagsByIds(final List<Long> tagIds) {
        log.info(LogMessages.TAGS_FETCH_BY_IDS_ATTEMPT.getMessage(), tagIds);
        List<Tag> tags = tagRepository.findAllById(tagIds);
        log.info(LogMessages.TAGS_FETCH_BY_IDS_SUCCESS.getMessage(), tags.size());
        return tags;
    }

    /**
     * Associates a post with multiple tags.
     *
     * @param tags   the tags to associate the post with
     * @param newPost the post to associate with the tags
     */
    public void addPostToTags(final List<Tag> tags, final Post newPost) {
        if (tags == null || tags.isEmpty() || tags.contains(null)) {
            log.error(LogMessages.TAGS_NOT_FOUND_OR_NULL.getMessage());
            return;
        }

        for (Tag tag : tags) {
            tag.getPosts().add(newPost);
            log.info(LogMessages.POST_ADDED_TO_TAG.getMessage(), newPost);
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
        log.info(LogMessages.POST_DELETE_FROM_TAGS_ATTEMPT.getMessage());
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
        log.info(LogMessages.POST_DELETE_FROM_TAG_ATTEMPT.getMessage(), tag.getName());
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
        log.info(LogMessages.TAGS_CREATION_ATTEMPT.getMessage(), tags.size());
        List<Tag> savedTags = tagRepository.saveAll(tags);
        log.info(LogMessages.TAGS_CREATION_SUCCESS.getMessage(), savedTags.size());
        return savedTags;
    }

    /**
     * Removes a user from the specified list of preferred tags.
     *
     * @param preferredTags the list of tags to remove the user from
     * @param userToDelete the user to remove from the tags
     */
    public void deleteUserFromTags(final List<Tag> preferredTags, final User userToDelete) {
        log.info(LogMessages.USER_DELETE_FROM_TAGS_ATTEMPT.getMessage(), preferredTags);
        if (preferredTags == null || preferredTags.isEmpty()) {
            log.error(LogMessages.TAGS_NOT_FOUND_OR_NULL.getMessage());
            return;
        }
        for (Tag tag : preferredTags) {
            if (tag != null) {
                tag.getUsers().remove(userToDelete);
            }
        }
        tagRepository.saveAll(preferredTags);
    }

    /**
     * Associates a user with multiple tags.
     *
     * @param tags the tags to associate the user with
     * @param user the user to associate with the tags
     */
    public void addUserToTags(final List<Tag> tags, final User user) {
        if (tags == null || tags.isEmpty() || tags.contains(null)) {
            log.error(LogMessages.TAGS_NOT_FOUND_OR_NULL.getMessage());
            return;
        }

        for (Tag tag : tags) {
            tag.getUsers().add(user);
            log.info(LogMessages.USER_ADDED_TO_TAG.getMessage(), user);
        }
        tagRepository.saveAll(tags);
    }
}
