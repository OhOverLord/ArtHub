/**
 * This package contains Data Transfer Object (DTO) classes for the Arthub backend application.
 * <p>
 * These DTOs are used to transfer data between different layers of the application,
 * such as between controllers and services.
 * </p>
 */
package org.arthub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data Transfer Object for post-related operations.
 * <p>
 * This DTO is used to transfer data when creating or updating posts, including the post's
 * title, description, associated tags, and an uploaded file.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    /**
     * The title of the post.
     */
    private String title;

    /**
     * A brief description of the post.
     */
    private String description;

    /**
     * A list of tag IDs associated with the post.
     */
    private List<Long> tagsId;

    /**
     * The file associated with the post, typically an image or document.
     */
    private MultipartFile file;
}
