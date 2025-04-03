package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.PostElasticsearch;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.repository.PostRepository;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
     void testGetPostById() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post foundPost = postService.getPostById(1L);

        assertNotNull(foundPost);
        assertEquals(post, foundPost);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
     void testCreatePost(){
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = postService.createPost(post);

        assertNotNull(savedPost);
        verify(postRepository, times(1)).save(post);
    }

    @Test
     void testUpdatePostPositive() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        Post newPost = new Post(1L, "full", "empty", null,  null, null, null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postElasticsearchRepository.findByPostId(1L)).thenReturn(Optional.of(new PostElasticsearch()));
        when(postRepository.save(any(Post.class))).thenReturn(newPost);

        Post updatedPost = postService.update(1L, newPost);
        assertEquals(updatedPost.getId(), post.getId());
        assertNotEquals(updatedPost.getDescription(), post.getDescription());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(newPost);
    }
    @Test
     void testUpdatePostNegative() {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        Post newPost = new Post(2L, "full", "empty", null,  null, null, null, null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(newPost);
        assertThrows(NotFound.class, () -> {
            postService.update(1L, newPost);
        });
    }
    @Test
     void testDeleteById() throws Exception {
        Post post = new Post(1L, "full", "full", null,  null, null, null, null);
        when(postElasticsearchRepository.findByPostId(1L)).thenReturn(Optional.of(new PostElasticsearch()));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);
        postService.deleteById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
     void testGetPostsById() {
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null,  null, null, null, null),
                new Post(2L, "user2", "user2@example.com",  null,  null, null, null, null)
        );
        when(postRepository.findAllById(List.of(1L, 2L))).thenReturn(mockPosts);
        List<Post> posts = postService.getPostsById(List.of(1L, 2L));
        assertNotNull(posts);
        assert(mockPosts.equals(posts));
        verify(postRepository, times(1)).findAllById(List.of(1L, 2L));
    }

    @Test
     void testAddFolderToPosts() {
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);

        postService.addFolderToPosts(mockPosts, new Folder(null, "shrek_photos", "", null, null));
        assert(mockPosts.stream().allMatch(post -> post.getFolders().size() == 1));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }

    @Test
     void testDeleteFolderFromPosts() {
        Folder folder = new Folder(null, "shrek_photos", "", null, null);
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", null, null,  null, new ArrayList<>(List.of(folder)), null),
                new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(List.of(folder)), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);
        postService.deleteFolderFromPosts(folder, mockPosts);
        assert(mockPosts.stream().allMatch(post -> post.getFolders().isEmpty()));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }

    @Test
     void testDeleteFolderFromPost() {
        Folder folder = new Folder(null, "shrek_photos", "", null, null);
        Post post = new Post(2L, "user2", "user2@example.com",  null, null,  null, new ArrayList<>(List.of(folder)), null);
        when(postRepository.save(any(Post.class))).thenReturn(null);
        postService.deleteFolderFromPost(folder, post);
        assertTrue(post.getFolders().isEmpty());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
     void testDeleteTagsFromPost() {
        Tag tag = TestEntities.getDefaultTag1();
        List<Post> mockPosts = List.of(
                new Post(1L, "user1", "user1@example.com", new ArrayList<>(List.of(tag)), null, null, new ArrayList<>(), null),
                new Post(2L, "user2", "user2@example.com",  new ArrayList<>(List.of(tag)),  null, null, new ArrayList<>(), null)
        );
        when(postRepository.saveAll(mockPosts)).thenReturn(null);

        postService.deleteTagsFromPost(tag, mockPosts);
        assert(mockPosts.stream().allMatch(post -> post.getTags().isEmpty()));
        verify(postRepository, times(1)).saveAll(mockPosts);
    }

    @Test
     void testSearchSuccessful() {
        PostElasticsearch postElasticsearch = TestEntities.ELASTICLONGDUMMY;
        Post post = new Post(1L, postElasticsearch.getTitle(),  postElasticsearch.getDescription(), null,  null, null, null, null);
        Pageable pageable = PageRequest.of(0, 5);
        when(postElasticsearchRepository.findByTitleOrDescriptionContaining("Life is", pageable)).thenReturn(new PageImpl<>(List.of(postElasticsearch), pageable, 1));
        when(postRepository.findAllByIds(List.of(1L), pageable)).thenReturn(new PageImpl<>(List.of(post), pageable, 1));

        Page<Post> foundPost = postService.search("Life is", pageable);
        assertFalse(foundPost.isEmpty());
        assertEquals(1L, (long) foundPost.getContent().get(0).getId());
    }

    @Test
     void testSearchUnsuccessful(){
        Pageable pageable = PageRequest.of(0, 5);
        when(postElasticsearchRepository.findByTitleOrDescriptionContaining("Death is", pageable)).thenReturn(Page.empty());

        Page<Post> foundPost = postService.search("Death is", pageable);
        assertTrue(foundPost.isEmpty());
    }
}
