/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to tag operations, including
 * retrieving, creating, and deleting tags.
 * </p>
 */
package org.arthub.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Facade.TagFacade;
import org.arthub.backend.Service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing tag-related operations.
 * <p>
 * Provides endpoints for retrieving all tags, retrieving a tag by ID,
 * creating new tags, and deleting a tag.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    /**
     * Service for handling tag-related operations.
     */
    private final TagService service;

    /**
     * Facade for performing complex tag-related operations.
     */
    private final TagFacade tagFacade;

    /**
     * Retrieves all tags.
     *
     * @return a {@link List} of all {@link Tag} entities
     */
    @GetMapping
    public List<Tag> getAllTags() {
        return service.readAll();
    }

    /**
     * Retrieves a tag by its ID.
     *
     * @param tagId the ID of the tag to retrieve
     * @return {@link ResponseEntity} containing the requested {@link Tag}
     * @throws NotFound if the tag with the specified ID is not found
     */
    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable final Long tagId) throws NotFound {
        Tag tag = service.getTagById(tagId);

        if (tag == null) {
            throw new NotFound();
        }
        return ResponseEntity.ok(tag);
    }

    /**
     * Creates new tags.
     *
     * @param tags the {@link List} of {@link Tag} entities to be created
     * @return {@link ResponseEntity} containing the list of newly created {@link Tag} entities
     */
    @PostMapping
    public ResponseEntity<List<Tag>> createTags(@Validated(Tag.class) @RequestBody final List<Tag> tags) {
        List<Tag> newTags = service.createTags(tags);
        return ResponseEntity.ok(newTags);
    }

    /**
     * Deletes a tag by its ID.
     *
     * @param tagId the ID of the tag to delete
     * @return {@link ResponseEntity} indicating the result of the deletion
     * @throws Exception if an error occurs during deletion
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable final Long tagId) throws Exception {
        tagFacade.deleteById(tagId);
        return ResponseEntity.ok().build();
    }
}