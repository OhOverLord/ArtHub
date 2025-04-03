package org.arthub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arthub.backend.entity.Post;

import java.util.List;

/**
 * Data Transfer Object for representing user profile information.
 * <p>
 * This DTO is used to transfer essential details about a user,
 * including their ID, username, email, and associated posts.
 * It is primarily used in responses to client requests.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The list of posts created by the user.
     */
    private List<Post> posts;
}
