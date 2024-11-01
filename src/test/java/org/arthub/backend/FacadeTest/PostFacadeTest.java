package org.arthub.backend.FacadeTest;

import org.arthub.backend.DTO.PostDTO;
import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Image;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Exception.NotFound;
import org.arthub.backend.Facade.PostFacade;
import org.arthub.backend.Service.*;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class PostFacadeTest {
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

    @Test
    public void testCreatePost() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        User user = TestEntities.getDefaultUser1();
        tag.setId(1L);
        user.setId(1L);
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null);
        PostDTO postDTO = new PostDTO("newPost", "newPost", new ArrayList<>(List.of(1L)), null);
        Post newPost = new Post();
        newPost.setId(1L);
        newPost.setTitle(post.getTitle());
        newPost.setDescription(post.getDescription());
        newPost.setPatron(user);

        
        //mocking inside createPost()
        when(jwtService.getUserByToken()).thenReturn(user);
        doNothing().when(userService).addPostToUser(any(User.class), any(Post.class));
        doNothing().when(tagService).addPostToTags(null, post);
        doNothing().when(imageService).addPostToImages(null, newPost);
        //mocking inside PostFromDTO()
        when(postService.createPost(any(Post.class))).thenReturn(newPost);
        //mocking inside setTagsInside()
        when(tagService.getTagById(1L)).thenReturn(tag);
        //mocking inside setImageInPost()
        when(imageService.createImage(null)).thenReturn(null);

        newPost = postFacade.createPost(postDTO);

        verify(userService, times(1)).addPostToUser(any(User.class), any(Post.class));
    }

    @Test
    public void testUpdatePostNoImageNonExistingNewTags() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null);
        PostDTO postDTO = new PostDTO("wow", "wow", new ArrayList<>(List.of(2L)), null);

        //mocking inside updatePost()
        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.update(1L, post)).thenReturn(post);
        //mocking inside updateTagsInPost()
        when(tagService.getTagsByIds(postDTO.getTagsId())).thenReturn(List.of());
        doNothing().when(tagService).deletePostFromTags(List.of(tag), post);

        Post updatedPost = postFacade.updatePost(1L, postDTO);
        assert(updatedPost.getTitle().equals(postDTO.getTitle()));
        assert(updatedPost.getDescription().equals(postDTO.getDescription()));
        verify(tagService, times(1)).deletePostFromTags(List.of(tag), post);
    }
    @Test
    public void testUpdatePostNoImageExistingNewTags() throws Exception {
        Tag tag = TestEntities.getDefaultTag1();
        Tag newTag = TestEntities.getDefaultTag2();
        tag.setId(1L);
        newTag.setId(2L);
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null);
        PostDTO postDTO = new PostDTO("wow", "wow", new ArrayList<>(List.of(2L)), null);

        //mocking inside updatePost()
        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.update(1L, post)).thenReturn(post);
        //mocking inside updateTagsInPost()
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
    public void testUpdatePostImageNonExistingNewTags() throws Exception {
        MultipartFile mf = TestEntities.createMockMultipartFile();
        Image image = new Image();
        image.setData(mf.getBytes());
        Tag tag = TestEntities.getDefaultTag1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(List.of(tag)),  null, null, null);
        PostDTO postDTO = new PostDTO("wow", "wow", new ArrayList<>(List.of(2L)), mf);

        //mocking inside updatePost()
        when(postService.getPostById(1L)).thenReturn(post);
        when(postService.update(1L, post)).thenReturn(post);
        //mocking inside updateTagsInPost()
        when(tagService.getTagsByIds(postDTO.getTagsId())).thenReturn(List.of());
        doNothing().when(tagService).deletePostFromTags(List.of(tag), post);
        //mocking inside updateImageInPost()
        when(imageService.createImage(postDTO.getFile())).thenReturn(image);

        Post updatedPost = postFacade.updatePost(1L, postDTO);
        assert(updatedPost.getTitle().equals(postDTO.getTitle()));
        assert(updatedPost.getDescription().equals(postDTO.getDescription()));
        assert(Arrays.equals(updatedPost.getImage().getData(), postDTO.getFile().getBytes()));
        verify(tagService, times(1)).deletePostFromTags(List.of(tag), post);
        verify(imageService, times(1)).createImage(postDTO.getFile());
    }
    @Test
    public void testDeleteByIdExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(),  null, new ArrayList<>(), user);

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
    public void testDeleteByIdNonExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(1L, "newPost", "newPost", new ArrayList<>(),  null, new ArrayList<>(), user);

        when(jwtService.getUserByToken()).thenReturn(user);
        when(postService.getPostById(2L)).thenReturn(null);

        assertThrows(Exception.class, () -> {
            postFacade.deletePostById(2L);
        });

    }
}
