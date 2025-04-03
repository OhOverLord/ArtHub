package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.springframework.security.core.Authentication;
import org.arthub.backend.dto.LoginUserDTO;
import org.arthub.backend.dto.RegisterUserDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testSignupSuccessful() throws AlreadyExists{
        User user = TestEntities.getDefaultUser1();
        RegisterUserDTO registerUserDTO = new RegisterUserDTO(user.getEmail(), user.getUsername(), "1234");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User user1 = authenticationService.signup(registerUserDTO);
        assertEquals(user, user1);
    }
    @Test
    void testSignupFailedEmail() {
        User user = TestEntities.getDefaultUser1();
        RegisterUserDTO registerUserDTO = new RegisterUserDTO(user.getEmail(), user.getUsername(), "1234");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExists.class, () -> authenticationService.signup(registerUserDTO));
    }
    @Test
    void testSignupFailedUsername() {
        User user = TestEntities.getDefaultUser1();
        RegisterUserDTO registerUserDTO = new RegisterUserDTO(user.getEmail(), user.getUsername(), "1234");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExists.class, () -> authenticationService.signup(registerUserDTO));
    }
    @Test
    void testAuthenticate(){
        LoginUserDTO loginUserDTO = new LoginUserDTO("john_doe", "1233");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(loginUserDTO.getUsername())).thenReturn(Optional.empty());

        User user = authenticationService.authenticate(loginUserDTO);
        assertNull(user);
    }
}
