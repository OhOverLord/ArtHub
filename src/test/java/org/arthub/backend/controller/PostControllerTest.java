package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.PostDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.facade.PostFacade;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostControllerTest {
    @Autowired
    private PostController postController;
    @MockBean
    private PostService postService;
    @MockBean
    private PostFacade postFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
     void testGetPostById() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(),  null, null, null, null);
        when(postService.getPostById(1L)).thenReturn(post);
        ResponseEntity<?> response = postController.getPostById(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());
        verify(postService, times(1)).getPostById(1L);
    }
    @Test
    void testGetPostsByPatronId(){
        List<Post> posts = new ArrayList<>(List.of(
                new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null, null),
                new Post(2L, "newPost", "newPost", new ArrayList<>(), null, null, null, null)
        ));
        when(postService.getPostsByPatronId(1L)).thenReturn(posts);
        ResponseEntity<?> response = postController.getPostsByPatronId(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(posts, response.getBody());
    }
    @Test
     void testCreatePost() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
        when(postFacade.createPost(postDTO)).thenReturn(post);
        ResponseEntity<?> response = postController.createPost(postDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());
        verify(postFacade, times(1)).createPost(postDTO);
    }
    @Test
     void testUpdatePost() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
        when(postFacade.updatePost(1L, postDTO)).thenReturn(post);
        ResponseEntity<?> response = postController.updatePost(1L, postDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());
        verify(postFacade, times(1)).updatePost(1L, postDTO);
    }
    @Test
     void testUpdateTestFailed() throws Exception {
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
        when(postFacade.updatePost(1L, postDTO)).thenThrow(new NotFound());
        NotFound thrown = assertThrows(NotFound.class, () -> {
            postController.updatePost(1L, postDTO);
        });
        assertEquals("Not Found", thrown.getMessage());
        verify(postFacade, times(1)).updatePost(1L, postDTO);
    }
    @Test
     void testDeletePost() throws Exception {
        doNothing().when(postFacade).deletePostById(1L);
        ResponseEntity<?> response = postController.deletePost(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(postFacade, times(1)).deletePostById(1L );
    }
    @Test
     void testSearchFound(){
        Post post = new Post(1L, TestEntities.ELASTICLONGDUMMY.getTitle(),  TestEntities.ELASTICLONGDUMMY.getDescription(), null,  null, null, null, null);
        Pageable pageable = PageRequest.of(0, 5);
        when(postService.search("Life is", pageable)).thenReturn(new PageImpl<>(List.of(post), pageable, 1));

        postController.getSearchResults("Life is", pageable);
        verify(postService, times(1)).search("Life is", pageable);
    }
    @Test
     void testSearchNotFound(){
        Pageable pageable = PageRequest.of(0, 5);
        when(postService.search("Death", pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 1));

        ResponseEntity<Page<Post>> response = postController.getSearchResults("Death", pageable);
        verify(postService, times(1)).search("Death", pageable);
        assert(Objects.requireNonNull(response.getBody()).isEmpty());
    }
}
