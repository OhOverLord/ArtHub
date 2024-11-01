package org.arthub.backend.ContollerTest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Service.RecommendationService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = DemoApplicationTests.class)
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = TestEntities.getDefaultUser1();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser
    public void testRecommendedPostsException() throws Exception {
        when(recommendationService.recommendedPosts(any(User.class), any(Pageable.class))).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/recommendations/posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }
}