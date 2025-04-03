package org.arthub.backend.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.service.TagService;
import org.arthub.backend.service.UserService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.arthub.backend.entity.Tag;

import java.util.List;

/**
 * Facade for managing user-related operations in the application.
 * This class provides an interface for handling user actions,
 * including deleting users and managing their preferred tags.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserFacade {

    /** Service for managing user-related operations. */
    private final UserService userService;

    /** Service for managing tag-related operations. */
    private final TagService tagService;

    /** Facade for managing post-related operations. */
    private final PostFacade postFacade;

    /**
     * Deletes a user by their ID.
     * This method removes the user's associations with preferred tags,
     * deletes their posts, and finally deletes the user from the database.
     *
     * @param userToDelete the user to delete
     * @throws Exception if an error occurs during deletion
     */
    @Transactional
    public void deleteUserById(final User userToDelete) throws NotFound {
        log.info("Deleting user: {}", userToDelete);
        List<Tag> userTags = userToDelete.getPreferredTags();
        tagService.deleteUserFromTags(userTags, userToDelete);
        userService.deletePreferedTags(userToDelete);
        List<Post> userPosts = userToDelete.getPosts();
        postFacade.deleteTagsFromPosts(userPosts);
        userService.deleteById(userToDelete.getId());
        log.info("User deleted: {}", userToDelete);
    }

    /**
     * Adds a list of tags to a user.
     * This method retrieves tags by their IDs
     * and associates them with the user.
     *
     * @param user the user to whom the tags will be added
     * @param tagIds the IDs of the tags to add
     */
    @Transactional
    public void addTagsToUser(final User user, final List<Long> tagIds) {
        log.info("Adding tags to user: {}", user);
        List<Tag> tags = tagService.getTagsByIds(tagIds);
        userService.addTagsToUser(user, tags);
        tagService.addUserToTags(tags, user);
        log.info("Tags added to user: {}", user);
    }
}
