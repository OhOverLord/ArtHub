/**
 * This package contains message enums for the Arthub backend application.
 * <p>
 * These enums represent standard log messages used across various services and controllers
 * to ensure consistent and informative logging throughout the application.
 * </p>
 */
package org.arthub.backend.messages;

import lombok.Getter;

/**
 * Enumeration of standardized log messages used in the Arthub backend application.
 * <p>
 * This enum provides predefined log messages to be used across different parts
 * of the application for logging events consistently and clearly.
 * Each constant represents a specific log message with placeholders (e.g., "{}")
 * that can be replaced with contextual information.
 * </p>
 *
 * @since 1.0
 */
@Getter
public enum LogMessages {

    /** Attempting to retrieve a folder by its ID. */
    FOLDER_RETRIEVE_ATTEMPT("Retrieving folder with ID: {}"),

    /** Indicates that the folder with the specified ID was not found. */
    FOLDER_NOT_FOUND("Folder not found for ID: {}"),

    /** Indicates successful retrieval of a folder by its ID. */
    FOLDER_RETRIEVED_SUCCESS("Successfully retrieved folder with ID: {}"),

    /** Indicates that the folder already exists. */
    FOLDER_ALREADY_EXISTS("Folder already exists"),

    /** Indicates that a folder has been saved to the database. */
    FOLDER_SAVED("Folder saved to database: {}"),

    /** Attempting to update a folder by its ID. */
    FOLDER_UPDATE_ATTEMPT("Attempting to update folder with ID: {}"),

    /** Indicates successful update of a folder by its ID. */
    FOLDER_UPDATE_SUCCESS("Successfully updated folder with ID: {}"),

    /** Attempting to delete a folder by its ID. */
    FOLDER_DELETE_ATTEMPT("Attempting to delete folder with ID: {}"),

    /** Indicates successful deletion of a folder by its ID. */
    FOLDER_DELETE_SUCCESS("Successfully deleted folder with ID: {}"),

    /** Indicates that a user was not found by the specified ID. */
    USER_NOT_FOUND("User not found for ID: {}"),

    /** Attempting to retrieve folders for a specific patron by ID. */
    FOLDERS_RETRIEVE_ATTEMPT("Attempting to retrieve folders for patron with ID: {}"),

    /** Indicates that folders have been retrieved for a specific patron. */
    FOLDERS_RETRIEVED("Retrieved {} folders for patron with ID: {}"),

    /** Indicates that a post was deleted from folders. */
    POST_DELETED_FROM_FOLDERS("Post deleted from folders: {}"),

    /** Retrieving folders for a patron using a JWT token. */
    FOLDERS_RETRIEVE_BY_TOKEN("Retrieving folders for the patron using JWT token."),

    /** Indicates that a user was not found from a JWT token. */
    USER_NOT_FOUND_BY_TOKEN("User not found from JWT token."),

    /** Attempting to register a new user with a specified username. */
    ATTEMPT_USER_REGISTRATION("Attempting to register a new user with username: {}"),

    /** Indicates registration failure due to an existing username or email. */
    USER_REGISTRATION_FAILED("Registration failed: User with username '{}' or email '{}' already exists."),

    /** Indicates successful user registration with the specified username. */
    USER_REGISTERED_SUCCESS("User registered successfully with username: {}"),

    /** Attempting to authenticate a user with a specified username. */
    ATTEMPT_USER_AUTHENTICATION("Authenticating user with username: {}"),

    /** Indicates successful user authentication. */
    USER_AUTHENTICATED_SUCCESS("User '{}' authenticated successfully."),

    /** Indicates that a user was not found after authentication. */
    USER_NOT_FOUND_AFTER_AUTHENTICATION("User '{}' not found after authentication."),

    /** Attempting to retrieve an image by its ID. */
    IMAGE_RETRIEVE_ATTEMPT("Retrieving image with ID: {}"),

    /** Indicates that an image was not found by the specified ID. */
    IMAGE_NOT_FOUND("Image not found for ID: {}"),

    /** Indicates successful retrieval of an image by its ID. */
    IMAGE_RETRIEVED_SUCCESS("Successfully retrieved image with ID: {}"),

    /** Indicates that a file is empty or null. */
    FILE_EMPTY_ERROR("File is empty or null"),

    /** Attempting to create a new image from a provided file. */
    IMAGE_CREATION_ATTEMPT("Creating a new image from the provided file: {}"),

    /** Indicates successful creation of an image. */
    IMAGE_CREATED_SUCCESS("Successfully created image with ID: {}"),

    /** Attempting to delete an image by its ID. */
    IMAGE_DELETE_ATTEMPT("Attempting to delete image with ID: {}"),

    /** Indicates successful deletion of an image by its ID. */
    IMAGE_DELETED_SUCCESS("Successfully deleted image with ID: {}"),

    /** Indicates that a post was added to images. */
    POST_ADDED_TO_IMAGE("Post added to images: {}"),

    /** Indicates that a post was removed from images. */
    POST_REMOVED_FROM_IMAGE("Post removed from images: {}"),

    /** Indicates that a post was not found by the specified ID. */
    POST_NOT_FOUND("Post not found for ID: {}"),

    /** Attempting to save a new post to the database. */
    POST_CREATION_ATTEMPT("Saving new post to database: {}"),

    /** Attempting to update a post. */
    POST_UPDATE_ATTEMPT("Updating post: {}"),

    /** Indicates update failure due to a non-existent post. */
    POST_UPDATE_FAILED("Update failed: Post not found with ID: {}"),

    /** Attempting to delete a post by its ID. */
    POST_DELETE_ATTEMPT("Attempting to delete post with ID: {}"),

    /** Indicates deletion failure due to a non-existent post or Elasticsearch entry. */
    POST_DELETE_FAILED("Deletion failed: Post or Elasticsearch entry not found for ID: {}"),

    /** Indicates successful deletion of a post and its Elasticsearch entry. */
    POST_DELETE_SUCCESS("Successfully deleted post and its Elasticsearch entry with ID: {}"),

    /** Indicates that posts have been found. */
    POSTS_FOUND("Posts found: {}"),

    /** Indicates that a folder was added to posts. */
    FOLDER_ADDED_TO_POSTS("Folder added to posts: {}"),

    /** Attempting to delete a folder from multiple posts. */
    FOLDER_DELETE_FROM_POSTS_ATTEMPT("Attempting to delete folder: {} from {} posts"),

    /** Indicates that a folder was removed from a specific post. */
    FOLDER_REMOVED_FROM_POST("Folder {} removed from post: {}"),

    /** Indicates that a folder was not found in a specific post. */
    FOLDER_NOT_FOUND_IN_POST("Folder {} not found in post: {}"),

    /** Indicates successful update of posts after deleting a folder. */
    POSTS_UPDATE_SUCCESS("Successfully updated posts after deleting folder: {}"),

    /** Attempting to delete a folder from a specific post. */
    FOLDER_DELETE_ATTEMPT_FROM_POST("Attempting to delete folder: {} from post: {}"),

    /** Indicates successful deletion of a folder from a specific post. */
    FOLDER_DELETE_SUCCESS_FROM_POST("Successfully removed folder {} from post: {}"),

    /** Indicates that a tag was removed from posts. */
    TAG_REMOVED_FROM_POSTS("Tag removed from posts: {}"),

    /** Attempting to search for posts with a specific keyword. */
    POST_SEARCH_ATTEMPT("Searching for posts with keyword: {}"),

    /** Indicates that no posts were found for a specific keyword. */
    POST_SEARCH_NO_RESULTS("No posts found for keyword: {}"),

    /** Indicates that search results were found in Elasticsearch. */
    POST_SEARCH_RESULTS_FOUND("Found {} posts in Elasticsearch. Retrieving from the database..."),

    /** Fetching all tags. */
    TAGS_FETCH_ALL("Fetching all tags"),

    /** Indicates successful retrieval of all tags. */
    TAGS_FETCH_SUCCESS("Successfully fetched tags, total count: {}"),

    /** Attempting to fetch a tag by ID. */
    TAG_FETCH_BY_ID_ATTEMPT("Fetching tag by ID: {}"),

    /** Indicates that a tag was not found by the specified ID. */
    TAG_NOT_FOUND("Tag not found with ID: {}"),

    /** Indicates successful retrieval of a tag by ID. */
    TAG_FETCH_BY_ID_SUCCESS("Successfully retrieved tag with ID: {}"),

    /** Attempting to create a new tag. */
    TAG_CREATION_ATTEMPT("Attempting to create a new tag: {}"),

    /** Indicates failure to create a tag due to an existing tag with the same name. */
    TAG_CREATION_FAILED_EXISTS("Tag creation failed: Tag already exists with name: {}"),

    /** Indicates successful creation of a tag. */
    TAG_CREATION_SUCCESS("Successfully created tag with ID: {}"),

    /** Attempting to delete a tag by its ID. */
    TAG_DELETE_ATTEMPT("Attempting to delete tag with ID: {}"),

    /** Indicates failure to delete a tag due to it not being found. */
    TAG_DELETE_FAILED_NOT_FOUND("Tag deletion failed: Tag not found with ID: {}"),

    /** Indicates successful deletion of a tag. */
    TAG_DELETE_SUCCESS("Successfully deleted tag with ID: {}"),

    /** Attempting to fetch tags by their IDs. */
    TAGS_FETCH_BY_IDS_ATTEMPT("Fetching tags by IDs: {}"),

    /** Indicates successful retrieval of tags by their IDs. */
    TAGS_FETCH_BY_IDS_SUCCESS("Successfully fetched tags, total count: {}"),

    /** Indicates that a post was added to tags. */
    POST_ADDED_TO_TAG("Post added to tags: {}"),

    /** Attempting to remove a post from tags. */
    POST_DELETE_FROM_TAGS_ATTEMPT("Remove post from Tags"),

    /** Attempting to remove a post from a specific tag. */
    POST_DELETE_FROM_TAG_ATTEMPT("Remove post from Tag: {}"),

    /** Attempting to create multiple tags without processing them individually. */
    TAGS_CREATION_ATTEMPT("Attempting to create multiple tags without processing. Total tags to save: {}"),

    /** Indicates successful creation of multiple tags. */
    TAGS_CREATION_SUCCESS("Successfully saved tags, total saved: {}"),

    /** Attempting to remove a user from tags. */
    USER_DELETE_FROM_TAGS_ATTEMPT("Delete user from tags: {}"),

    /** Indicates that a user was added to tags. */
    USER_ADDED_TO_TAG("User added to tags: {}"),

    /** Fetching all users. */
    USERS_FETCH_ALL("Fetching all users"),

    /** Indicates successful retrieval of all users. */
    USERS_FETCH_SUCCESS("Successfully fetched users, total count: {}"),

    /** Attempting to fetch a user by ID. */
    USER_FETCH_BY_ID_ATTEMPT("Fetching user by ID: {}"),

    /** Indicates that a user was not found by the specified ID. */
    USER_NOT_FOUND_BY_ID("User not found with ID: {}"),

    /** Indicates successful retrieval of a user by ID. */
    USER_FETCH_BY_ID_SUCCESS("Successfully retrieved user with ID: {}"),

    /** Attempting to fetch a user by username. */
    USER_FETCH_BY_USERNAME_ATTEMPT("Fetching user by username: {}"),

    /** Indicates that a user was not found with the specified username. */
    USER_NOT_FOUND_BY_USERNAME("User not found with the provided username: {}"),

    /** Indicates successful retrieval of a user by username. */
    USER_FETCH_BY_USERNAME_SUCCESS("Successfully retrieved user by username: {}"),

    /** Attempting to fetch a user by email. */
    USER_FETCH_BY_EMAIL_ATTEMPT("Fetching user by email: {}"),

    /** Indicates that a user was not found with the specified email. */
    USER_NOT_FOUND_BY_EMAIL("User not found with the provided email: {}"),

    /** Indicates successful retrieval of a user by email. */
    USER_FETCH_BY_EMAIL_SUCCESS("Successfully retrieved user by email: {}"),

    /** Attempting to update user information by user ID. */
    USER_UPDATE_ATTEMPT("Updating user information for user ID: {}"),

    /** Indicates failure to update due to an existing username. */
    USER_UPDATE_USERNAME_EXISTS("Username already exists: {}"),

    /** Indicates failure to update due to an existing email. */
    USER_UPDATE_EMAIL_EXISTS("Email already exists: {}"),

    /** Indicates successful user information update. */
    USER_UPDATE_SUCCESS("User information updated successfully for user ID: {}"),

    /** Attempting to create a new user. */
    USER_CREATION_ATTEMPT("Creating a new user"),

    /** Indicates user creation failure due to an existing username or email. */
    USER_CREATION_FAILED_EXISTS("User creation failed: username or email already exists"),

    /** Indicates successful user creation. */
    USER_CREATION_SUCCESS("User created successfully with ID: {}"),

    /** Attempting to delete a user by ID. */
    USER_DELETE_ATTEMPT("Attempting to delete user with ID: {}"),

    /** Indicates failure to delete user due to non-existent user ID. */
    USER_DELETE_FAILED_NOT_FOUND("User not found with ID: {}"),

    /** Indicates successful deletion of user. */
    USER_DELETE_SUCCESS("User deleted successfully with ID: {}"),

    /** Attempting to add a post to a user by ID. */
    USER_ADD_POST_ATTEMPT("Attempting to add post to user ID: {}"),

    /** Indicates successful addition of a post to a user by ID. */
    USER_ADD_POST_SUCCESS("Successfully added post to user ID: {}"),

    /** Indicates that a post already exists for the user ID. */
    USER_POST_ALREADY_EXISTS("Post already exists for user ID: {}"),

    /** Attempting to delete a post from a user by ID. */
    USER_DELETE_POST_ATTEMPT("Delete post from User ID: {}"),

    /** Attempting to add a folder to a user by ID. */
    USER_ADD_FOLDER_ATTEMPT("Attempting to add folder to user ID: {}"),

    /** Indicates successful addition of a folder to a user by ID. */
    USER_ADD_FOLDER_SUCCESS("Successfully added folder to user ID: {}"),

    /** Indicates that a folder already exists for the user ID. */
    USER_FOLDER_ALREADY_EXISTS("Folder already exists for user ID: {}"),

    /** Attempting to delete a folder from a user by ID. */
    USER_DELETE_FOLDER_ATTEMPT("Delete folder from User ID: {}"),

    /** Attempting to delete all tags from a user by ID. */
    USER_DELETE_TAGS_ATTEMPT("Delete all tags from User ID: {}"),

    /** Indicates failure to add tags to a user due to null tags. */
    TAGS_NOT_FOUND_OR_NULL("Tags not found or null"),

    /** Indicates that a tag was added to a user. */
    TAG_ADDED_TO_USER("Tag added to user: {}");

    /**
     * Template message to logging.
     */
    private final String message;

    /**
     * Constructor for LogMessages.
     *
     * @param message the log message template associated with the enum constant
     */
    LogMessages(final String message) {
        this.message = message;
    }
}
