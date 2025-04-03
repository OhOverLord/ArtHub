/**
 * This package contains entity classes for the Arthub backend application.
 * <p>
 * These entities represent the core data models used within the application, including users, posts, tags, and other
 * related entities. They are mapped to database tables and are used for persistence.
 * </p>
 */
package org.arthub.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity class representing an image.
 * <p>
 * This entity represents an image in the database, including its data and the related post.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "image")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image implements Serializable {

    /** Unique identifier for ensuring serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 2405172041950251805L;

    /**
     * The unique identifier for the image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The binary data of the image.
     */
    private String file;

    /**
     * The post associated with the image.
     * <p>
     * This is a bidirectional one-to-one mapping where the post refers back to the image.
     * </p>
     */
    @OneToOne(mappedBy = "image")
    @JsonBackReference
    @JsonIgnore
    private Post post;

    /**
     * Returns the hash code of the image.
     *
     * @return the hash code of the image
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Checks if this image is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Image image = (Image) obj;
        return id.equals(image.id);
    }
}
