package org.arthub.backend.ContollerTest;

import org.arthub.backend.Controller.PostController;
import org.arthub.backend.DTO.PostDTO;
import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Facade.PostFacade;
import org.arthub.backend.Service.FolderService;
import org.arthub.backend.Service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class PostControllerTest {
    @Autowired
    private PostController postController;
    @MockBean
    private PostService postService;
    @MockBean
    private PostFacade postFacade;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testGetPostById() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(),  null, null, null);
        when(postService.getPostById(1L)).thenReturn(post);
        ResponseEntity<?> response = postController.getPostById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(post, response.getBody());
        verify(postService, times(1)).getPostById(1L);
    }
    @Test
    public void testCreatePost() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
        when(postFacade.createPost(postDTO)).thenReturn(post);
        ResponseEntity<?> response = postController.createPost(postDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(post, response.getBody());
        verify(postFacade, times(1)).createPost(postDTO);
    }
    @Test
    public void testUpdatePost() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
        when(postFacade.updatePost(1L, postDTO)).thenReturn(post);
        ResponseEntity<?> response = postController.updatePost(1L, postDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(post, response.getBody());
        verify(postFacade, times(1)).updatePost(1L, postDTO);
    }
//    @Test
//    public void testUpdateTestFailed() throws Exception {
//        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(), null);
//        when(postFacade.updatePost(1L, postDTO)).thenThrow(new NotFound());
//        ResponseEntity<?> response = postController.updatePost(1L, postDTO);
//        assertEquals(404, response.getStatusCodeValue());
//        verify(postFacade, times(1)).updatePost(1L, postDTO);
//    }
    @Test
    public void testDeletePost() throws Exception {
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null);
        doNothing().when(postFacade).deletePostById(1L);
        ResponseEntity<?> response = postController.deletePost(1L);
        assertEquals(200, response.getStatusCodeValue());
        verify(postFacade, times(1)).deletePostById(1L );
    }
}
