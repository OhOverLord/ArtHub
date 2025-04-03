package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.entity.User;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class RecommendationServiceTest {
    @Autowired
    private RecommendationService recommendationService;
    @MockBean
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testGetGuestPosts(){
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null, null, null, new ArrayList<>(), null)
        );
        when(postRepository.findAll(TestEntities.PAGEABLE)).thenReturn(new PageImpl<>(mockPosts, TestEntities.PAGEABLE, mockPosts.size()));

        Page<Post> foundPosts = recommendationService.getGuestPosts(TestEntities.PAGEABLE);
        assertEquals(mockPosts.size(), foundPosts.getTotalElements());
    }

    @Test
    void testRecommendedPostsSuccessful(){
        User user = TestEntities.getDefaultUser1();
        user.setPreferredTags(new ArrayList<>(List.of(TestEntities.getDefaultTag1())));
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(), null)
        );
        List<String> combinedTags = Collections.singletonList(TestEntities.getDefaultTag1().getName());
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;

        when(associationServiceClient.getAssociations(any(List.class))).thenReturn(List.of());
        when(postElasticsearchRepository.findPostsExcludingTags(combinedTags, TestEntities.PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(postElasticsearch), TestEntities.PAGEABLE, 1));
        when(postElasticsearchRepository.findPostsWithoutTags(TestEntities.PAGEABLE)).thenReturn(Page.empty());
        when(postElasticsearchRepository.findPostsByTags(combinedTags, TestEntities.PAGEABLE)).thenReturn(Page.empty());
        when(postService.readAllByIds(List.of(1L), TestEntities.PAGEABLE)).thenReturn(new PageImpl<>(mockPosts, TestEntities.PAGEABLE, mockPosts.size()));

        Page<Post> recommendedPosts = recommendationService.recommendedPosts(user, TestEntities.PAGEABLE);
        assertEquals(mockPosts.size(), recommendedPosts.getTotalElements());
        verify(postService, times(1)).readAllByIds(List.of(1L), TestEntities.PAGEABLE);
    }
    @Test
    void testRecommendedPostsEmptyPreferred(){
        User user = TestEntities.getDefaultUser1();
        user.setPreferredTags(new ArrayList<>());
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(), null)
        );

        when(postRepository.findAll(TestEntities.PAGEABLE)).thenReturn(new PageImpl<>(mockPosts, TestEntities.PAGEABLE, mockPosts.size()));

        Page<Post> recommendedPosts = recommendationService.recommendedPosts(user, TestEntities.PAGEABLE);
        assertEquals(mockPosts.size(), recommendedPosts.getTotalElements());
        verify(postService, times(0)).readAllByIds(List.of(1L), TestEntities.PAGEABLE);
    }
    @Test
    void testRecommendedPostsEmptyCombined(){
        User user = TestEntities.getDefaultUser1();
        user.setPreferredTags(new ArrayList<>(List.of(TestEntities.getDefaultTag1())));
        user.setPreferredTags(new ArrayList<>());
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(), null)
        );
        List<String> combinedTags = Collections.singletonList(TestEntities.getDefaultTag1().getName());

        when(associationServiceClient.getAssociations(any(List.class))).thenReturn(List.of());
        when(postElasticsearchRepository.findPostsExcludingTags(combinedTags, TestEntities.PAGEABLE)).thenReturn(Page.empty());
        when(postElasticsearchRepository.findPostsWithoutTags(TestEntities.PAGEABLE)).thenReturn(Page.empty());
        when(postElasticsearchRepository.findPostsByTags(combinedTags, TestEntities.PAGEABLE)).thenReturn(Page.empty());
        when(postRepository.findAll(TestEntities.PAGEABLE)).thenReturn(new PageImpl<>(mockPosts, TestEntities.PAGEABLE, mockPosts.size()));

        Page<Post> recommendedPosts = recommendationService.recommendedPosts(user, TestEntities.PAGEABLE);
        assertEquals(mockPosts.size(), recommendedPosts.getTotalElements());
        verify(postService, times(0)).readAllByIds(List.of(1L), TestEntities.PAGEABLE);
    }
}
