package org.arthub.backend.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.service.RecommendationService;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
class RecommendationControllerTest {

    @Autowired
    private RecommendationController recommendationController;
    @MockBean
    private RecommendationService recommendationService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testGetGuestPosts(){
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, null, null, null),
                new Post(2L, "user2", "user2@example.com",  null,  null, null, null, null)
        );
        Pageable pageable = TestEntities.PAGEABLE;
        when(recommendationService.getGuestPosts(pageable)).thenReturn(new PageImpl<>(mockPosts, pageable, mockPosts.size()));

        Page<Post> foundPosts = recommendationService.getGuestPosts(pageable);
        assertEquals(2, foundPosts.getTotalElements());
    }
}