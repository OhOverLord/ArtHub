package org.arthub.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.ServiceUnavailableException;
import org.arthub.backend.messages.ExceptionMessages;
import org.arthub.backend.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service class for managing post recommendations.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    /**
     * Repository for managing Post entities in the database.
     */
    private final PostRepository postRepository;
    /**
     * Repository for managing Post elastic search entities in the database.
     */
    private final PostElasticsearchRepository postElasticsearchRepository;
    /**
     * Service for post entity.
     */
    private final PostService postService;
    /**
     * client for python association service.
     */
    private final AssociationServiceClient associationServiceClient;

    /**
     * Retrieves recommended posts for the given user.
     *
     * @param currentUser the current user for whom to retrieve recommendations
     * @param pageable    pagination information
     * @return a page of recommended posts
     */
    public Page<Post> recommendedPosts(
            final User currentUser, final Pageable pageable
    ) throws ServiceUnavailableException {
        List<Tag> preferredTags = currentUser.getPreferredTags();

        if (preferredTags.isEmpty()) {
            return this.getGuestPosts(pageable);
        }

        List<String> tagNames = preferredTags.stream()
                .map(Tag::getName)
                .toList();

        List<String> recommendedTags;
        try {
            recommendedTags = associationServiceClient.getAssociations(tagNames);
        } catch (Exception ex) {
            throw new ServiceUnavailableException(ExceptionMessages.SERVICE_UNAVAIABLE.getMessage(), ex);
        }

        List<String> combinedTags = Stream.concat(tagNames.stream(), recommendedTags.stream())
                .distinct()
                .toList();

        Page<PostElasticsearch> postsWithTags = postElasticsearchRepository.findPostsByTags(combinedTags, pageable);
        Page<PostElasticsearch> postsExcludingTags =
                postElasticsearchRepository.findPostsExcludingTags(combinedTags, pageable);
        Page<PostElasticsearch> postsWithoutTags = postElasticsearchRepository.findPostsWithoutTags(pageable);

        List<PostElasticsearch> combinedPosts = Stream.concat(
                        Stream.concat(
                                postsWithTags.getContent().stream(),
                                postsExcludingTags.getContent().stream()
                        ),
                        postsWithoutTags.getContent().stream()
                )
                .distinct()
                .toList();

        if (combinedPosts.isEmpty()) {
            return this.getGuestPosts(pageable);
        }

        List<Long> postIds = combinedPosts.stream()
                .map(PostElasticsearch::getPostId)
                .toList();

        return postService.readAllByIds(postIds, pageable);
    }


    /**
     * Retrieves guest posts for users who are not logged in.
     *
     * @param pageable pagination information
     * @return a page of guest posts
     */
    public Page<Post> getGuestPosts(final Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> guestPosts = new ArrayList<>(posts.getContent());

        return new PageImpl<>(guestPosts, pageable, posts.getTotalElements());
    }
}
