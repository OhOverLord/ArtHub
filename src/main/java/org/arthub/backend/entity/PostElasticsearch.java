/**
 * This package contains entity classes for the Arthub backend application.
 * <p>
 * These entities represent the core data models used within the application,
 * including posts, users, and tags, which are mapped to the relational database
 * and Elasticsearch for faster search operations.
 * </p>
 */
package org.arthub.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Entity class representing a post in Elasticsearch.
 * <p>
 * This class is used for indexing posts in Elasticsearch for faster search operations.
 * It contains the same basic fields as the regular post entity, but with specific annotations
 * for Elasticsearch indexing.
 * </p>
 *
 * @since 1.0
 */
@Document(indexName = "feed")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostElasticsearch {

    /**
     * The unique identifier for the post in Elasticsearch.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * The title of the post, indexed as text in Elasticsearch.
     */
    @Field(type = FieldType.Text)
    private String title;

    /**
     * The description of the post, indexed as text in Elasticsearch.
     */
    @Field(type = FieldType.Text)
    private String description;

    /**
     * The ID of the post in the relational database (used to link with the actual post).
     */
    @Field(type = FieldType.Integer)
    private Long postId;

    /**
     * The tags associated with the post, stored as a list of keywords in Elasticsearch.
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;
}
