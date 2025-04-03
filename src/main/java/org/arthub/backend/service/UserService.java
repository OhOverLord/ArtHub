package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.dto.UserProfileDTO;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.InvalidPassword;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.mapper.UserMapper;
import org.arthub.backend.messages.LogMessages;
import org.arthub.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing user-related operations.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    /**
     * The repository for managing user entities.
     */
    private final UserRepository userRepository;

    /**
     * Service for encoding and decoding user passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Service for handling basic post-related operations.
     */
    private final PostService postService;

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> readAll() {
        log.info(LogMessages.USERS_FETCH_ALL.getMessage());
        List<User> users = userRepository.findAll();
        log.info(LogMessages.USERS_FETCH_SUCCESS.getMessage(), users.size());
        return users;
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws NotFound if the user is not found
     */
    public User readUserById(final Long userId) throws NotFound {
        log.info(LogMessages.USER_FETCH_BY_ID_ATTEMPT.getMessage(), userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND_BY_ID.getMessage(), userId);
            throw new NotFound();
        }
        log.info(LogMessages.USER_FETCH_BY_ID_SUCCESS.getMessage(), userId);
        return user;
    }

    /**
     * Retrieves the profile of a user by their ID.
     *
     * @param userId the ID of the user whose profile is being retrieved.
     * @return a {@link UserProfileDTO} containing the user's profile data.
     * @throws NotFound if no user is found with the given ID.
     */
    public UserProfileDTO getUserProfile(final Long userId) throws NotFound {
        log.info(LogMessages.USER_FETCH_BY_ID_ATTEMPT.getMessage(), userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND_BY_ID.getMessage(), userId);
            throw new NotFound();
        }
        log.info(LogMessages.USER_FETCH_BY_ID_SUCCESS.getMessage(), userId);
        List<Post> userPosts = postService.getPostsByPatronId(userId);
        UserProfileDTO result = UserMapper.toUserProfileDTO(user);
        result.setPosts(userPosts);
        return result;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user to retrieve
     * @return the user with the specified username
     * @throws NotFound if the user is not found
     */
    public User readUserByUsername(final String username) throws NotFound {
        log.info(LogMessages.USER_FETCH_BY_USERNAME_ATTEMPT.getMessage(), username);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND_BY_USERNAME.getMessage(), username);
            throw new NotFound();
        }
        log.info(LogMessages.USER_FETCH_BY_USERNAME_SUCCESS.getMessage(), username);
        return user;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email of the user to retrieve
     * @return the user with the specified email
     * @throws NotFound if the user is not found
     */
    public User readUserByEmail(final String email) throws NotFound {
        log.info(LogMessages.USER_FETCH_BY_EMAIL_ATTEMPT.getMessage(), email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn(LogMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email);
            throw new NotFound();
        }
        log.info(LogMessages.USER_FETCH_BY_EMAIL_SUCCESS.getMessage(), email);
        return user;
    }

    /**
     * Updates a user's information.
     *
     * @param currentUser the current user
     * @param updateUser  the user object containing updated information
     * @return the updated user
     * @throws AlreadyExists if the updated username or email already exists
     */
    public User update(final User currentUser, final User updateUser) throws AlreadyExists {
        log.info(LogMessages.USER_UPDATE_ATTEMPT.getMessage(), currentUser.getId());

        if (userRepository.findByUsername(updateUser.getUsername()).isPresent()
                && !currentUser.getUsername().equals(updateUser.getUsername())) {
            log.warn(LogMessages.USER_UPDATE_USERNAME_EXISTS.getMessage(), updateUser.getUsername());
            throw new AlreadyExists("username");
        }
        if (userRepository.findByEmail(updateUser.getEmail()).isPresent()
                && !currentUser.getEmail().equals(updateUser.getEmail())) {
            log.warn(LogMessages.USER_UPDATE_EMAIL_EXISTS.getMessage(), updateUser.getEmail());
            throw new AlreadyExists("email");
        }

        currentUser.fromUser(updateUser);
        userRepository.save(currentUser);
        log.info(LogMessages.USER_UPDATE_SUCCESS.getMessage(), currentUser.getId());
        return currentUser;
    }
    /**
     * Updates the password of the current user.
     *
     * @param currentUser      the user whose password needs to be updated.
     * @param currentPassword  the current password of the user for validation.
     * @param newPassword      the new password to be set for the user.
     * @param confirmPassword  the confirmation of the new password to ensure they match.
     * @return the updated {@link User} entity with the new password.
     * @throws InvalidPassword if the provided current password does not match the user's actual password.
     * @throws ConfirmPasswordIsNotEqual if the new password and confirmation password do not match.
     */

    public User updatePassword(final User currentUser,
                               final String currentPassword,
                               final String newPassword,
                               final String confirmPassword) throws InvalidPassword, ConfirmPasswordIsNotEqual {
        log.info("Attempting to update password for user with ID: {}", currentUser.getId());

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            log.warn("Failed password update for user ID: {}. Reason: Current password does not match.",
                    currentUser.getId());
            throw new InvalidPassword();
        }

        if (!newPassword.equals(confirmPassword)) {
            log.warn("Failed password update for user ID: {}. Reason: New password and confirmation do not match.",
                    currentUser.getId());
            throw new ConfirmPasswordIsNotEqual();
        }

        log.info("Password validation successful for user ID: {}. Proceeding with password update.",
                currentUser.getId());
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        log.info("Password successfully updated for user ID: {}", currentUser.getId());

        return currentUser;
    }


    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     * @throws AlreadyExists if the username or email already exists
     */
    public User createUser(final User user) throws AlreadyExists {
        log.info(LogMessages.USER_CREATION_ATTEMPT.getMessage());

        if (userRepository.findByUsername(user.getUsername()).isPresent()
                || userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn(LogMessages.USER_CREATION_FAILED_EXISTS.getMessage());
            throw new AlreadyExists();
        }

        User createdUser = userRepository.save(user);
        log.info(LogMessages.USER_CREATION_SUCCESS.getMessage(), createdUser.getId());
        return createdUser;
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the ID of the user to delete
     * @throws NotFound if the user is not found
     */
    public void deleteById(final Long userId) throws NotFound {
        log.info(LogMessages.USER_DELETE_ATTEMPT.getMessage(), userId);

        User userToDelete = userRepository.findById(userId).orElse(null);
        if (userToDelete == null) {
            log.warn(LogMessages.USER_DELETE_FAILED_NOT_FOUND.getMessage(), userId);
            throw new NotFound();
        }

        userRepository.delete(userToDelete);
        log.info(LogMessages.USER_DELETE_SUCCESS.getMessage(), userId);
    }

    /**
     * Adds a post to a user's list of posts.
     *
     * @param patron  the user to add the post to
     * @param newPost the post to add
     */
    public void addPostToUser(final User patron, final Post newPost) {
        log.info(LogMessages.USER_ADD_POST_ATTEMPT.getMessage(), patron.getId());

        if (!patron.getPosts().contains(newPost)) {
            patron.getPosts().add(newPost);
            userRepository.save(patron);
            log.info(LogMessages.USER_ADD_POST_SUCCESS.getMessage(), patron.getId());
        } else {
            log.info(LogMessages.USER_POST_ALREADY_EXISTS.getMessage(), patron.getId());
        }
    }

    /**
     * Deletes a post from a user's list of posts.
     *
     * @param patron the user to delete the post from
     * @param post   the post to delete
     */
    public void deletePostFromUser(final User patron, final Post post) {
        log.info(LogMessages.USER_DELETE_POST_ATTEMPT.getMessage(), patron.getId());
        patron.getPosts().remove(post);
        userRepository.save(patron);
    }

    /**
     * Adds a folder to a user's list of folders.
     *
     * @param patron    the user to add the folder to
     * @param newFolder the folder to add
     */
    public void addFolderToUser(final User patron, final Folder newFolder) {
        log.info(LogMessages.USER_ADD_FOLDER_ATTEMPT.getMessage(), patron.getId());
        if (!patron.getFolders().contains(newFolder)) {
            patron.getFolders().add(newFolder);
            userRepository.save(patron);
            log.info(LogMessages.USER_ADD_FOLDER_SUCCESS.getMessage(), patron.getId());
        } else {
            log.info(LogMessages.USER_FOLDER_ALREADY_EXISTS.getMessage(), patron.getId());
        }
    }

    /**
     * Deletes a folder from a user's list of folders.
     *
     * @param patron         the user to delete the folder from
     * @param folderToDelete the folder to delete
     */
    public void deleteFolderFromUser(final User patron, final Folder folderToDelete) {
        log.info(LogMessages.USER_DELETE_FOLDER_ATTEMPT.getMessage(), patron.getId());
        patron.getFolders().remove(folderToDelete);
        userRepository.save(patron);
    }

    /**
     * Deletes all preferred tags from a user.
     *
     * @param userToDelete the user to delete tags from
     */
    public void deletePreferedTags(final User userToDelete) {
        log.info(LogMessages.USER_DELETE_TAGS_ATTEMPT.getMessage(), userToDelete.getId());
        userToDelete.getPreferredTags().clear();
        userRepository.save(userToDelete);
    }

    /**
     * Adds tags to a user's list of preferred tags.
     *
     * @param user the user to add tags to
     * @param tags the tags to add
     */
    public void addTagsToUser(final User user, final List<Tag> tags) {
        if (tags == null || tags.isEmpty() || tags.contains(null)) {
            log.error(LogMessages.TAGS_NOT_FOUND_OR_NULL.getMessage());
            return;
        }

        tags.forEach(tag -> {
            if (!user.getPreferredTags().contains(tag)) {
                user.getPreferredTags().add(tag);
                log.info(LogMessages.TAG_ADDED_TO_USER.getMessage(), tag);
            }
        });
        userRepository.save(user);
    }
}
