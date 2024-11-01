/**
 * This package contains controller classes for the Arthub backend application.
 * <p>
 * These controllers handle HTTP requests and responses related to recommendations, including
 * recommended posts for authenticated users and guest users.
 * </p>
 */
package org.arthub.backend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Service.RecommendationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.arthub.backend.Entity.Post;

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
     * @throws Exception if an error occurs during the recommendation process
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> recommendedPosts(final Pageable pageable) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Page<Post> posts = recommendationService.recommendedPosts(currentUser, pageable);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("Error: {}", e.toString());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves posts for guest users.
     *
     * @param pageable the pagination information
     * @return {@link ResponseEntity} containing a page of posts for guest users
     * @throws Exception if an error occurs during the retrieval process
     */
    @GetMapping("/guest")
    public ResponseEntity<Page<Post>> getGuestPosts(final Pageable pageable) throws Exception {
        Page<Post> guestPosts = recommendationService.getGuestPosts(pageable);
        return ResponseEntity.ok(guestPosts);
    }
}
