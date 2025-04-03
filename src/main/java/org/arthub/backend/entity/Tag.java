/**
 * This package contains entity classes for the Arthub backend application.
 * <p>
 * These entities represent the core data models used within the application, including users, posts, tags, and other
 * related entities. They are mapped to database tables and are used for persistence.
 * </p>
 */
package org.arthub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class representing a tag.
 * <p>
 * This entity represents a tag in the database, including its name, the posts it's associated with,
 * and the users who prefer this tag.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {

    /** Unique identifier for ensuring serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 2405172041950251807L;

    /**
     * The unique identifier for the tag.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The name of the tag. The length is limited to 30 characters, and it must be unique.
     */
    @Column(name = "name", length = 30, unique = true)
    private String name;

    /**
     * The list of posts associated with this tag.
     */
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    /**
     * The list of users who prefer this tag.
     */
    @ManyToMany(mappedBy = "preferredTags", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    /**
     * Returns a string representation of the tag.
     *
     * @return a string representation of the tag
     */
    public String toString() {
        return "Tag: " + this.name;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
