package org.arthub.backend.Facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Service.PostService;
import org.arthub.backend.Service.TagService;
import org.springframework.stereotype.Service;

/**
 * Facade for managing tags in the application.
 * This class provides an interface for handling operations related to tags,
 * including deleting tags and their associations with posts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagFacade {

    /** Service for managing tag-related operations. */
    private final TagService tagService;

    /** Service for managing post-related operations. */
    private final PostService postService;

    /**
     * Deletes a tag by its ID.
     * This method first retrieves the tag from the database.
     * If the tag is found, it removes all associations
     * of the tag with its related posts before deleting the tag.
     *
     * @param tagId the ID of the tag to delete
     * @throws Exception if the tag is not found
     * or any error occurs during deletion
     */
    @Transactional
    public void deleteById(final Long tagId) throws Exception {
        Tag tagToDelete = tagService.getTagById(tagId);

        if (tagToDelete == null) {
            throw new NotFound();
        }
        postService.deleteTagsFromPost(tagToDelete, tagToDelete.getPosts());
        tagService.deleteById(tagId);
    }
}
