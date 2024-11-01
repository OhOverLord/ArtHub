package org.arthub.backend.ServiceTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.PostElasticsearch;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Repository.PostRepository;
import org.arthub.backend.Service.PostService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testGetPostById() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post foundPost = postService.getPostById(1L);

        assertNotNull(foundPost);
        assert(foundPost.equals(post));
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreatePost(){
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = postService.createPost(post);

        assertNotNull(savedPost);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testUpdatePostPositive() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        Post newPost = new Post(1L, "full", "empty", null,  null, null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postElasticsearchRepository.findByPostId(1L)).thenReturn(Optional.of(new PostElasticsearch()));
        when(postRepository.save(any(Post.class))).thenReturn(newPost);

        Post updatedPost = postService.update(1L, newPost);
        assert(updatedPost.getId().equals(post.getId()));
        assert(!updatedPost.getDescription().equals(post.getDescription()));
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(newPost);
    }
    @Test
    public void testUpdatePostNegative() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        Post newPost = new Post(2L, "full", "empty", null,  null, null, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(newPost);
        assertThrows(NotFound.class, () -> {
            postService.update(1L, newPost);
        });
    }
    @Test
    public void testDeleteById() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null);
        when(postElasticsearchRepository.findByPostId(1L)).thenReturn(Optional.of(new PostElasticsearch()));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);
        postService.deleteById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void testGetPostsById() {
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, null, null),
                new Post(2L, "user2", "user2@example.com",  null,  null, null, null)
        );
        when(postRepository.findAllById(List.of(1L, 2L))).thenReturn(mockPosts);
        List<Post> posts = postService.getPostsById(List.of(1L, 2L));
        assertNotNull(posts);
        assert(mockPosts.equals(posts));
        verify(postRepository, times(1)).findAllById(List.of(1L, 2L));
    }

    @Test
    public void testAddFolderToPosts() {
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null,  null, new ArrayList<>(), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);

        postService.addFolderToPosts(mockPosts, new Folder(null, "shrek_photos", "", null, null));
        assert(mockPosts.stream().allMatch(post -> post.getFolders().size() == 1));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }

    @Test
    public void testDeleteFolderFromPosts() {
        Folder folder = new Folder(null, "shrek_photos", "", null, null);
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, new ArrayList<>(List.of(folder)), null),
                new Post(2L, "user2", "user2@example.com",  null,  null, new ArrayList<>(List.of(folder)), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);
        postService.deleteFolderFromPosts(folder, mockPosts);
        assert(mockPosts.stream().allMatch(post -> post.getFolders().isEmpty()));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }

    @Test
    public void testDeleteFolderFromPost() {
        Folder folder = new Folder(null, "shrek_photos", "", null, null);
        Post post = new Post(2L, "user2", "user2@example.com",  null,  null, new ArrayList<>(List.of(folder)), null);
        when(postRepository.save(any(Post.class))).thenReturn(null);
        postService.deleteFolderFromPost(folder, post);
        assert(post.getFolders().isEmpty());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testDeleteTagsFromPost() {
        Tag tag = TestEntities.getDefaultTag1();
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", new ArrayList<>(List.of(tag)),  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  new ArrayList<>(List.of(tag)),  null, new ArrayList<>(), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);

        postService.deleteTagsFromPost(tag, mockPosts);
        assert(mockPosts.stream().allMatch(post -> post.getTags().isEmpty()));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }
}
