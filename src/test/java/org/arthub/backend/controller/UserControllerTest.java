package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.UserProfileDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.Post;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.facade.UserFacade;
import org.arthub.backend.mapper.UserMapper;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.service.JwtService;
import org.arthub.backend.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;
    @MockBean
    private  UserService userService;
    @MockBean
    private  UserFacade userFacade;
    @MockBean
    private  JwtService jwtService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetUserByUsername(){
        User user = TestEntities.getDefaultUser1();
        Authentication authenticationMock = mock(Authentication.class);
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContextMock);

        ResponseEntity<?> response = userController.getUserByUsername();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(response.getBody(), user);
    }

    @Test
    void testAddTags(){
        User user = TestEntities.getDefaultUser1();
        Authentication authenticationMock = mock(Authentication.class);
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(user);
        doNothing().when(userFacade).addTagsToUser(user, List.of(1L, 2L));
        SecurityContextHolder.setContext(securityContextMock);

        ResponseEntity<?> response = userController.getUserByUsername();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(response.getBody(), user);
    }
    @Test
    void testUpdateUser() throws AlreadyExists{
        User user = TestEntities.getDefaultUser1();
        User user1 = TestEntities.getDefaultUser2();
        Authentication authenticationMock = mock(Authentication.class);
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(user);
        when(userService.update(user, user1)).thenReturn(user1);
        when(jwtService.generateToken(user1)).thenReturn("");
        SecurityContextHolder.setContext(securityContextMock);

        ResponseEntity<?> response = userController.updateUser(user1);
        assertEquals(200, response.getStatusCode().value());
    }
    @Test
    void testDeleteUser() throws NotFound {
        User user = TestEntities.getDefaultUser1();
        Authentication authenticationMock = mock(Authentication.class);
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(user);
        doNothing().when(userFacade).deleteUserById(user);
        SecurityContextHolder.setContext(securityContextMock);

        ResponseEntity<?> response = userController.deleteUser();
        assertEquals(200, response.getStatusCode().value());
    }
    @Test
    void testGetUserByIdWithPosts() throws NotFound {
        // prepare objects
        User user = TestEntities.getDefaultUser1();
        user.setId(1L);
        List<Post> posts = TestEntities.getListWithDefaultPosts(3);
        user.setPosts(posts);
        // prepare application
        Authentication authenticationMock = mock(Authentication.class);
        SecurityContext securityContextMock = mock(SecurityContext.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(user);
        when(userService.getUserProfile(user.getId())).thenReturn(UserMapper.toUserProfileDTO(user));
        SecurityContextHolder.setContext(securityContextMock);

        ResponseEntity<?> response = userController.getUserProfile(user.getId());
        assertEquals(200, response.getStatusCode().value());
        UserProfileDTO userProfileDTO = UserMapper.toUserProfileDTO(user);
        assertEquals(response.getBody(), userProfileDTO);
    }
}
