package org.arthub.backend.ServiceTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.Entity.Folder;
import org.arthub.backend.Entity.Post;
import org.arthub.backend.Entity.Tag;
import org.arthub.backend.Entity.User;
import org.arthub.backend.Repository.TagRepository;
import org.arthub.backend.Repository.UserRepository;
import org.arthub.backend.Service.UserService;
import org.arthub.backend.TestData.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;

    @Test
    public void testDeleteByIdExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        try {
            userService.deleteById(user.getId());
        }catch(Exception ignored){}
        verify(userRepository, times(1)).delete(user);
    }
    @Test
    public void testDeleteByIdNonExisting() throws Exception {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(userRepository).delete(user);
        try {
            userService.deleteById(user.getId());
        }catch(Exception ignored){}
        verify(userRepository, times(0)).delete(user);
    }
    @Test
    public void testAddPostToUserPositive(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null);
        user.setPosts(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addPostToUser(user, post);
        assert(user.getPosts().contains(post));
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testAddPostToUserNegative(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null);
        user.setPosts(new ArrayList<>(List.of(post)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addPostToUser(user, post);
        assert(user.getPosts().size() == 1);
        verify(userRepository, times(0)).save(user);
    }
    @Test
    public void testDeletePostFromUser(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null);
        user.setPosts(new ArrayList<>(List.of(post)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deletePostFromUser(user, post);
        assert(user.getPosts().isEmpty());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testAddFolderToUserPositive(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addFolderToUser(user, folder);
        assert(user.getFolders().contains(folder));
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testAddFolderToUserNegative(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>(List.of(folder)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addFolderToUser(user, folder);
        assert(user.getFolders().contains(folder));
        verify(userRepository, times(0)).save(user);
    }
    @Test
    public void testDeleteFolderFromUser(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>(List.of(folder)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deleteFolderFromUser(user, folder);
        assert(user.getFolders().isEmpty());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    public void testAddTagsToUser(){
        User user = TestEntities.getDefaultUser1();
        Tag tag1 = TestEntities.getDefaultTag1();
        Tag tag2 = TestEntities.getDefaultTag2();
        tag1.setId(1L);
        tag2.setId(2L);
        user.setPreferredTags(new ArrayList<>());

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addTagsToUser(user, new ArrayList<>(List.of(tag1, tag2)));

        assert(user.getPreferredTags().size() == 2);
        assert(user.getPreferredTags().equals(List.of(tag1,tag2)));
        verify(userRepository, times(1)).save(user);
    }

}
