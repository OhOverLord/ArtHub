/**
 * This package contains entity classes for the Arthub backend application.
 * <p>
 * These entities represent the core data models used within the application, including users, posts, tags, and other
 * related entities. They are mapped to database tables and are used for persistence.
 * </p>
 */
package org.arthub.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Entity class representing a folder.
 * <p>
 * This entity represents a folder in the database, containing information such as
 * the title, description, the associated patron (user), and the posts in the folder.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "folder")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Folder implements Serializable {

    /** Unique identifier for ensuring serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 2405172041950251804L;

    /**
     * The unique identifier for the folder.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The title of the folder.
     */
    @Column(name = "title", length = 40)
    private String title;

    /**
     * A brief description of the folder.
     */
    @Column(name = "description")
    private String description;

    /**
     * The patron (user) associated with the folder.
     */
    @ManyToOne
    @JoinTable(
            name = "patron_folder",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private User patron;

    /**
     * The list of posts associated with the folder.
     */
    @ManyToMany
    @JoinTable(
            name = "folder_post",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @JsonManagedReference
    private List<Post> posts;

    /**
     * Returns a string representation of the folder.
     *
     * @return a string representation of the folder, including its title and description
     */
    @Override
    public String toString() {
        return "Folder: " + this.title + " " + this.description;
    }
}
