package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.UserProfileDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Folder;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.Tag;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.InvalidPassword;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.mapper.UserMapper;
import org.arthub.backend.repository.TagRepository;
import org.arthub.backend.repository.UserRepository;
import org.arthub.backend.mock.TestEntities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void testReadAll(){
        List<User> users = new ArrayList<>(List.of(
                TestEntities.getDefaultUser1(),
                TestEntities.getDefaultUser2()
        ));
        when(userRepository.findAll()).thenReturn(users);
        List<User> users2 = userService.readAll();
        assertEquals(2, users2.size());
        assertEquals(users, users2);
    }
    @Test
    void testReadUserByIdSuccessful() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User user2 = userService.readUserById(user.getId());
        assertEquals(user, user2);
    }
    @Test
    void testReadUserByIdFailed() {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> userService.readUserById(user.getId()));
    }
    @Test
    void testReadUserByUsernameSuccessful() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User user2 = userService.readUserByUsername(user.getUsername());
        assertEquals(user, user2);
    }
    @Test
    void testReadUserByUsernameFailed() {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> userService.readUserByUsername(user.getUsername()));
    }
    @Test
    void testReadUserByEmailSuccessful() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User user2 = userService.readUserByEmail(user.getEmail());
        assertEquals(user, user2);
    }
    @Test
    void testReadUserByEmailFailed() {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> userService.readUserByEmail(user.getEmail()));
    }
    @Test
    void testCreateUserSuccessful() throws AlreadyExists{
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User user1 = userService.createUser(user);
        assertEquals(user1, user);
    }
    @Test
    void testCreateUserFailedUsername(){
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExists.class, () -> userService.createUser(user));
    }
    @Test
    void testCreateUserFailedEmail(){
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(AlreadyExists.class, () -> userService.createUser(user));
    }
    @Test
    void testDeletePreferedTags() {
        User user = TestEntities.getDefaultUser1();
        user.setPreferredTags(new ArrayList<>(List.of(TestEntities.getDefaultTag1())));
        when(userRepository.save(user)).thenReturn(user);

        userService.deletePreferedTags(user);
        assertTrue(user.getPreferredTags().isEmpty());
    }
    @Test
    void testDeleteByIdExisting()throws NotFound {
        User user = TestEntities.getDefaultUser1();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userService.deleteById(user.getId());
        verify(userRepository, times(1)).delete(user);
    }
    @Test
    void testDeleteByIdNonExisting() {
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(userRepository).delete(user);
        assertThrows(NotFound.class, () -> userService.deleteById(user.getId()));
        verify(userRepository, times(0)).delete(user);
    }
    @Test
    void testAddPostToUserPositive(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
        user.setPosts(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addPostToUser(user, post);
        assert(user.getPosts().contains(post));
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testAddPostToUserNegative(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
        user.setPosts(new ArrayList<>(List.of(post)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addPostToUser(user, post);
        assert(user.getPosts().size() == 1);
        verify(userRepository, times(0)).save(user);
    }
    @Test
    void testDeletePostFromUser(){
        User user = TestEntities.getDefaultUser1();
        Post post = new Post(null, "full", "full", null,  null, null, null, null);
        user.setPosts(new ArrayList<>(List.of(post)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deletePostFromUser(user, post);
        assert(user.getPosts().isEmpty());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testAddFolderToUserPositive(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addFolderToUser(user, folder);
        assert(user.getFolders().contains(folder));
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testAddFolderToUserNegative(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>(List.of(folder)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addFolderToUser(user, folder);
        assert(user.getFolders().contains(folder));
        verify(userRepository, times(0)).save(user);
    }
    @Test
    void testDeleteFolderFromUser(){
        User user = TestEntities.getDefaultUser1();
        Folder folder = new Folder(null, "shrek_photos", "", user, null);
        user.setFolders(new ArrayList<>(List.of(folder)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deleteFolderFromUser(user, folder);
        assert(user.getFolders().isEmpty());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testAddTagsToUser(){
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
    @Test
    void testUpdateSuccessful() throws AlreadyExists {
        User user = TestEntities.getDefaultUser1();
        User user1 = TestEntities.getDefaultUser2();
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.update(user, user1);
        assertEquals(updatedUser.getEmail(), user1.getEmail());
        assertEquals(updatedUser.getUsername(), user1.getUsername());
    }
    @Test
    void testUpdateExistingUsername() {
        User user = TestEntities.getDefaultUser1();
        User user1 = TestEntities.getDefaultUser2();
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        AlreadyExists exists = assertThrows(AlreadyExists.class, () -> {
            userService.update(user,user1);
        });
        assertTrue(exists.getMessage().contains("username"));
    }
    @Test
    void testUpdateExistingEmail() {
        User user = TestEntities.getDefaultUser1();
        User user1 = TestEntities.getDefaultUser2();
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        AlreadyExists exists = assertThrows(AlreadyExists.class, () -> {
            userService.update(user,user1);
        });
        assertTrue(exists.getMessage().contains("email"));
    }

    @Test
    void testReadUserProfileByIdSuccessful() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        UserProfileDTO expectedProfile = UserMapper.toUserProfileDTO(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserProfileDTO actualProfile = userService.getUserProfile(user.getId());

        assertEquals(expectedProfile, actualProfile);
    }

    @Test
    void testReadUserProfileByIdNotFound() {
        User user = TestEntities.getDefaultUser1();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> userService.getUserProfile(user.getId()));
    }

    @Test
    void testUpdatePasswordSuccessful() throws InvalidPassword, ConfirmPasswordIsNotEqual {
        User user = TestEntities.getDefaultUser1();


        String currentPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        // Set up the mock to return the user with the expected current password
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Update password
        user.setPassword(passwordEncoder.encode(currentPassword));  // Simulate the current password

        User updatedUser = userService.updatePassword(user, currentPassword, newPassword, confirmPassword);

        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));  // Verify password is updated
        verify(userRepository, times(1)).save(updatedUser);  // Verify save was called
    }

    @Test
    void testUpdatePasswordInvalidCurrentPassword() {
        User user = TestEntities.getDefaultUser1();

        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        user.setPassword(passwordEncoder.encode("oldPassword"));  // Simulate the current password

        // Test for invalid current password
        InvalidPassword exception = assertThrows(InvalidPassword.class, () -> {
            userService.updatePassword(user, currentPassword, newPassword, confirmPassword);
        });
        assertNotNull(exception);  // Ensure the exception is thrown
    }

    @Test
    void testUpdatePasswordConfirmPasswordMismatch() {
        User user = TestEntities.getDefaultUser1();
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "differentPassword";

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        user.setPassword(passwordEncoder.encode("oldPassword"));  // Simulate the current password

        // Test for confirmation password mismatch
        ConfirmPasswordIsNotEqual exception = assertThrows(ConfirmPasswordIsNotEqual.class, () -> {
            userService.updatePassword(user, currentPassword, newPassword, confirmPassword);
        });
        assertNotNull(exception);  // Ensure the exception is thrown
    }
}

