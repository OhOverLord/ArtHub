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

import java.util.List;

/**
 * Data Transfer Object for folder-related operations.
 * <p>
 * This DTO is used to transfer folder data, including the folder's title, description,
 * and a list of associated post IDs.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderDTO {

    /**
     * The title of the folder.
     */
    private String title;

    /**
     * A brief description of the folder.
     */
    private String description;

    /**
     * A list of post IDs associated with the folder.
     */
    private List<Long> postIds;
}
