/**
 * This package contains mapper classes for converting between different entity types
 * in the Arthub backend application.
 * <p>
 * These mappers are responsible for transforming data between various formats,
 * such as converting between database entities and Elasticsearch entities.
 * </p>
 */
package org.arthub.backend.mapper;

import lombok.experimental.UtilityClass;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.entity.Tag;

import java.util.List;

/**
 * Mapper class for converting between {@link Post} and {@link PostElasticsearch} entities.
 * <p>
 * This class provides static methods for transforming {@link Post} objects into
 * {@link PostElasticsearch} objects and for mapping tags into a list of their names.
 * </p>
 *
 * @since 1.0
 */
@UtilityClass
public class PostMapper {

    /**
     * Converts a {@link Post} entity into a {@link PostElasticsearch} entity.
     *
     * @param post the {@link Post} entity to convert
     * @return the corresponding {@link PostElasticsearch} entity, or {@code null} if the input is {@code null}
     */
    public static PostElasticsearch toElasticsearchEntity(final Post post) {
        if (post == null) {
            return null;
        }

        return PostElasticsearch.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .tags(mapTags(post.getTags()))
                .build();
    }

    /**
     * Maps a list of {@link Tag} entities to a list of their names.
     *
     * @param tags the list of {@link Tag} entities to map
     * @return a list of tag names, or {@code null} if the input list is {@code null}
     */
    public static List<String> mapTags(final List<Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(Tag::getName)
                .toList();
    }
}
