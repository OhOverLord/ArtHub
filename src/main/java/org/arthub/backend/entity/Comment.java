package org.arthub.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a comment on a post.
 * Contains the text of the comment, the associated post, author, and timestamp.
 */
@Entity
@Data
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment implements Serializable {

    /**
     * The unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The text content of the comment.
     */
    @Column(name = "text")
    private String text;

    /**
     * The post to which the comment belongs.
     */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Post post;

    /**
     * The username of the comment's author.
     */
    private String authorUsername;

    /**
     * The date and time when the comment was created.
     */
    @Column(name = "date")
    private LocalDateTime date;
}
