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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a post.
 * <p>
 * This entity represents a post in the database, including its title, description,
 * associated tags, image, folders, and the patron (user) who created it.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    /** Unique identifier for ensuring serialization compatibility. */
    @Serial
    private static final long serialVersionUID = 2405172041950251806L;

    /**
     * The unique identifier for the post.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the post.
     */
    @Column(name = "title", length = 40)
    private String title;

    /**
     * A brief description of the post.
     */
    @Column(name = "description")
    private String description;

    /**
     * The list of tags associated with the post.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonManagedReference
    private List<Tag> tags = new ArrayList<>();

    /**
     * List of comments for this post.
     * Maps to the "post" field in the {@link Comment} entity.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();


    /**
     * The image associated with the post.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    /**
     * The list of folders that contain the post.
     */
    @ManyToMany(mappedBy = "posts")
    @JsonBackReference
    @JsonIgnore
    private List<Folder> folders = new ArrayList<>();

    /**
     * The patron (user) who created the post.
     */
    @ManyToOne
    @JoinTable(
            name = "post_patron",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    @JsonManagedReference
    private User patron;

    /**
     * Returns a string representation of the post.
     *
     * @return a string representation of the post
     */
    @Override
    public String toString() {
        return "Post: " + this.title + " " + this.description;
    }

    /**
     * Returns the hash code of the post.
     *
     * @return the hash code of the post
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Checks if this post is equal to another object.
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
        Post post = (Post) obj;
        return id.equals(post.id);
    }
}
