package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.AlreadyExists;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.UserRepository;
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
     * Repository for managing User entities in the database.
     */
    private final UserRepository userRepository;

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> readAll() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Successfully fetched users, total count: {}", users.size());
        return users;
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws NotFound if the user is not found
     */
    public User readUserById(final Long userId) throws Exception {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found with ID: {}", userId);
            throw new NotFound();
        }
        log.info("Successfully retrieved user with ID: {}", userId);
        return user;
    }


    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user to retrieve
     * @return the user with the specified username
     * @throws NotFound if the user is not found
     */
    public User readUserByUsername(final String username) throws Exception {
        log.info("Fetching user by username");
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn("User not found with the provided username");
            throw new NotFound();
        }
        log.info("Successfully retrieved user by username");
        return user;
    }


    /**
     * Retrieves a user by email.
     *
     * @param email the email of the user to retrieve
     * @return the user with the specified email
     * @throws NotFound if the user is not found
     */
    public User readUserByEmail(final String email) throws Exception {
        log.info("Fetching user by email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("User not found with the provided email");
            throw new NotFound();
        }
        log.info("Successfully retrieved user by email");
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
        log.info("Updating user information for user ID: {}", currentUser.getId());

        if (userRepository.findByUsername(updateUser.getUsername()).isPresent()
                && !currentUser.getUsername().equals(updateUser.getUsername())) {
            log.warn("Username already exists: {}", updateUser.getUsername());
            throw new AlreadyExists("username");
        }
        if (userRepository.findByEmail(updateUser.getEmail()).isPresent()
                && !currentUser.getEmail().equals(updateUser.getEmail())) {
            log.warn("Email already exists: {}", updateUser.getEmail());
            throw new AlreadyExists("email");
        }

        currentUser.fromUser(updateUser);
        userRepository.save(currentUser);
        log.info("User information updated successfully for user ID: {}", currentUser.getId());
        return currentUser;
    }


    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     * @throws AlreadyExists if the username or email already exists
     */
    public User createUser(final User user) throws Exception {
        log.info("Creating a new user");

        if (userRepository.findByUsername(user.getUsername()).isPresent()
                || userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("User creation failed: username or email already exists");
            throw new AlreadyExists();
        }

        User createdUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return createdUser;
    }


    /**
     * Deletes a user by ID.
     *
     * @param userId the ID of the user to delete
     * @throws NotFound if the user is not found
     */
    public void deleteById(final Long userId) throws Exception {
        log.info("Attempting to delete user with ID: {}", userId);

        User userToDelete = userRepository.findById(userId).orElse(null);
        if (userToDelete == null) {
            log.warn("User not found with ID: {}", userId);
            throw new NotFound();
        }

        userRepository.delete(userToDelete);
        log.info("User deleted successfully with ID: {}", userId);
    }


    // Methods for managing posts, folders, and tags associated with users...
    /**
     * Adds a post to a user's list of posts.
     *
     * @param patron  the user to add the post to
     * @param newPost the post to add
     */
    public void addPostToUser(final User patron, final Post newPost) {
        log.info("Attempting to add post to user ID: {}", patron.getId());

        if (!patron.getPosts().contains(newPost)) {
            patron.getPosts().add(newPost);
            userRepository.save(patron);
            log.info("Successfully added post to user ID: {}", patron.getId());
        } else {
            log.info("Post already exists for user ID: {}", patron.getId());
        }
    }


    /**
     * Deletes a post from a user's list of posts.
     *
     * @param patron the user to delete the post from
     * @param post   the post to delete
     */
    public void deletePostFromUser(final User patron, final Post post) {
        log.info("Delete post from User: {}", patron);
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
        log.info("Adding folder in User: {}", patron);
        if (!patron.getFolders().contains(newFolder)) {
            patron.getFolders().add(newFolder);
            userRepository.save(patron);
            log.info("Folder added to user: {}", patron);
        } else {
            log.info("Folder already in User: {}", patron);
        }
    }

    /**
     * Deletes a folder from a user's list of folders.
     *
     * @param patron         the user to delete the folder from
     * @param folderToDelete the folder to delete
     */
    public void deleteFolderFromUser(final User patron, final Folder folderToDelete) {
        log.info("Delete folder from User: {}", patron);
        patron.getFolders().remove(folderToDelete);
        userRepository.save(patron);
    }

    /**
     * Deletes all preferred tags from a user.
     *
     * @param userToDelete the user to delete tags from
     */
    public void deletePreferedTags(final User userToDelete) {
        log.info("Delete tags from User: {}", userToDelete);
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
            log.error("Tags not found or null");
            return;
        }

        tags.forEach(tag -> {
            if (!user.getPreferredTags().contains(tag)) {
                user.getPreferredTags().add(tag);
                log.info("Tag added to user: {}", tag);
            }
        });
        userRepository.save(user);
    }
}
