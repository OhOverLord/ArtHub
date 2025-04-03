/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to recommendations, including
 * recommended posts for authenticated users and guest users.
 * </p>
 */
package org.arthub.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.entity.User;
import org.arthub.backend.service.RecommendationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.arthub.backend.entity.Post;

/**
 * Controller class for handling recommendation-related operations.
 * <p>
 * Provides endpoints for retrieving recommended posts for authenticated users and guest users.
 * </p>
 *
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
@Slf4j
public class RecommendationController {

    /**
     * Service for handling recommendation-related logic.
     */
    private final RecommendationService recommendationService;

    /**
     * Retrieves recommended posts for the currently authenticated user.
     *
     * @param pageable the pagination information
     * @return {@link ResponseEntity} containing a page of recommended posts
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> recommendedPosts(final Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Page<Post> posts = recommendationService.recommendedPosts(currentUser, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieves posts for guest users.
     *
     * @param pageable the pagination information
     * @return {@link ResponseEntity} containing a page of posts for guest users
     */
    @GetMapping("/guest")
    public ResponseEntity<Page<Post>> getGuestPosts(final Pageable pageable) {
        Page<Post> guestPosts = recommendationService.getGuestPosts(pageable);
        return ResponseEntity.ok(guestPosts);
    }
}
