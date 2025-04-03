package org.arthub.backend.facade;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.PostDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.service.*;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostFacadeTest {
    @Autowired
    private PostFacade postFacade;
    @MockBean
    private PostService postService;
    @MockBean
    private TagService tagService;
    @MockBean
    private UserService userService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private FolderService folderService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testCreatePost() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        User user = TestEntities.getDefaultUser1();
        tag.setId(1L);
        user.setId(1L);
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(List.of(1L)), null);
        Post newPost = new Post();
        newPost.setId(1L);
        newPost.setTitle(post.getTitle());
        newPost.setDescription(post.getDescription());
        newPost.setPatron(user);


        when(jwtService.getUserByToken()).thenReturn(user);
        doNothing().when(userService).addPostToUser(any(User.class), any(Post.class));
        doNothing().when(tagService).addPostToTags(null, post);
        doNothing().when(imageService).addPostToImages(null, newPost);
        when(postService.createPost(any(Post.class))).thenReturn(newPost);
        when(tagService.getTagById(1L)).thenReturn(tag);
        when(imageService.createImage(null)).thenReturn(null);

        postFacade.createPost(postDTO);

        verify(userService, times(1)).addPostToUser(any(User.class), any(Post.class));
    }

    @Test
    void testUpdatePostNoImageNonExistingNewTags() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null, null);
        PostDTO postDTO = new PostDTO("wow", "wow", new ArrayList<>(List.of(2L)), null);

        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.update(1L, post)).thenReturn(post);
        when(tagService.getTagsByIds(postDTO.getTagsId())).thenReturn(List.of());
        doNothing().when(tagService).deletePostFromTags(List.of(tag), post);

        Post updatedPost = postFacade.updatePost(1L, postDTO);
        assert(updatedPost.getTitle().equals(postDTO.getTitle()));
        assert(updatedPost.getDescription().equals(postDTO.getDescription()));
        verify(tagService, times(1)).deletePostFromTags(List.of(tag), post);
    }
    @Test
    void testUpdatePostNoImageExistingNewTags() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        Tag newTag = TestEntities.getDefaultTag2();
        tag.setId(1L);
        newTag.setId(2L);
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null, null);
        PostDTO postDTO = new PostDTO("wow", "wow", new ArrayList<>(List.of(2L)), null);

        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.update(1L, post)).thenReturn(post);
        when(tagService.getTagsByIds(postDTO.getTagsId())).thenReturn(List.of(newTag));
        doNothing().when(tagService).deletePostFromTags(List.of(tag), post);
        doNothing().when(tagService).deletePostFromTag(any(Tag.class), any(Post.class));
        doNothing().when(tagService).addPostToTags(List.of(newTag), post);

        Post updatedPost = postFacade.updatePost(1L, postDTO);
        assert(updatedPost.getTitle().equals(postDTO.getTitle()));
        assert(updatedPost.getDescription().equals(postDTO.getDescription()));
        assert(updatedPost.getTags().equals(List.of(newTag)));
        verify(tagService, times(0)).deletePostFromTags(List.of(tag), post);
        verify(tagService, times(1)).deletePostFromTag(any(Tag.class), any(Post.class));
        verify(tagService, times(1)).addPostToTags(List.of(newTag), post);
        verify(postService, times(1)).update(1L, post);
    }
    @Test
    void testDeleteByIdExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(), null , null, new ArrayList<>(), user);

        when(jwtService.getUserByToken()).thenReturn(user);
        when(postService.getPostById(1L)).thenReturn(post);
        doNothing().when(folderService).deletePostFromFolders(List.of(), post);
        doNothing().when(userService).deletePostFromUser(user, post);
        doNothing().when(tagService).deletePostFromTags(List.of(), post);
        doNothing().when(postService).deleteById(post.getId());

        postFacade.deletePostById(1L);
        verify(folderService, times(1)).deletePostFromFolders(List.of(), post);
        verify(userService, times(1)).deletePostFromUser(user, post);
        verify(tagService, times(1)).deletePostFromTags(List.of(), post);
        verify(postService, times(1)).deleteById(post.getId());
    }
    @Test
    void testDeleteByIdNonExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();

        when(jwtService.getUserByToken()).thenReturn(user);
        when(postService.getPostById(2L)).thenReturn(null);

        assertThrows(Exception.class, () -> {
            postFacade.deletePostById(2L);
        });
    }
    @Test
    void testDeleteTagsFromPostsSuccessful(){
        List<Post> posts = new ArrayList<>(List.of(
                new Post(1L, "newPost", "newPost", new ArrayList<>(), null, null, null, null),
                new Post(2L, "newPost", "newPost", new ArrayList<>(), null, null, null, null)
        ));
        doNothing().when(postService).saveAll(posts);

        postFacade.deleteTagsFromPosts(posts);
        verify(postService, times(1)).saveAll(posts);
    }
    @Test
    void testDeleteTagsFromPostsFailed(){
        postFacade.deleteTagsFromPosts(List.of());
        verify(postService, times(0)).saveAll(List.of());
    }
}
