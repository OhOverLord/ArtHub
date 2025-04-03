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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entity class representing a user.
 * <p>
 * This entity represents a user in the database, including their username, email, password, associated folders,
 * posts, and preferred tags.
 * </p>
 *
 * @since 1.0
 */
@Entity
@Table(name = "patron")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The username of the user.
     */
    @Column(name = "username", unique = true, length = 30)
    private String username;

    /**
     * The list of password reset tokens associated with the user.
     */
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private transient List<PasswordResetToken> passwordResetToken;

    /**
     * The email address of the user.
     */
    @Column(name = "email", unique = true, length = 30)
    private String email;

    /**
     * The password of the user. This field is ignored in JSON responses.
     */
    @Column(name = "password")
    @JsonIgnore
    private String password;

    /**
     * The list of folders associated with the user.
     */
    @OneToMany(mappedBy = "patron")
    @JsonBackReference
    @JsonIgnore
    private transient List<Folder> folders = new ArrayList<>();

    /**
     * The list of preferred tags associated with the user.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_tags", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> preferredTags;

    /**
     * The list of posts created by the user.
     */
    @OneToMany(mappedBy = "patron", fetch = FetchType.EAGER)
    @JsonBackReference
    @JsonIgnore
    private transient List<Post> posts = new ArrayList<>();

    /**
     * Sets user attributes from another user.
     *
     * @param user the user from which attributes are copied
     */
    public void fromUser(final User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    /**
     * Returns a string representation of the user.
     *
     * @return a string representation of the user, including username and email
     */
    @Override
    public String toString() {
        return "User: " + this.username + " " + this.email;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return an empty collection, as no authorities are assigned to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true, as accounts do not expire
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     *
     * @return true, as accounts are not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * @return true, as credentials do not expire
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true, as all users are enabled by default
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
