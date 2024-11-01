package org.arthub.backend.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * Retrieves recommended posts for the given user.
     *
     * @param currentUser the current user for whom to retrieve recommendations
     * @param pageable    pagination information
     * @return a page of recommended posts
     */
    public Page<Post> recommendedPosts(final User currentUser, final Pageable pageable) {
        List<Tag> preferredTags = currentUser.getPreferredTags();
        Page<Post> taggedPosts = preferredTags.isEmpty()
                ? Page.empty(pageable)
                : postRepository.findByTagsIn(preferredTags, pageable);

        int halfPageSize = Math.max(1, pageable.getPageSize() / 2);
        Pageable halfPageable = PageRequest.of(pageable.getPageNumber(), halfPageSize);
        Page<Post> randomPosts = postRepository.findRandom(halfPageable, preferredTags);

        List<Post> mixedPosts = new ArrayList<>();
        mixedPosts.addAll(taggedPosts.getContent());
        mixedPosts.addAll(randomPosts.getContent());
        Collections.shuffle(mixedPosts);

        return new PageImpl<>(mixedPosts, pageable,
                taggedPosts.getTotalElements() + randomPosts.getTotalElements());
    }

    /**
     * Retrieves guest posts for users who are not logged in.
     *
     * @param pageable pagination information
     * @return a page of guest posts
     */
    public Page<Post> getGuestPosts(final Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> guestPosts = new ArrayList<>();
        guestPosts.addAll(posts.getContent());

        return new PageImpl<>(guestPosts, pageable, posts.getTotalElements());
    }
}
