package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.LoginUserDTO;
import org.arthub.backend.dto.RegisterUserDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.AlreadyExists;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.responses.LoginResponse;
import org.arthub.backend.service.AuthenticationService;
import org.arthub.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthenticationControllerTest {
    @Autowired
    private AuthenticationController authenticationController;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testRegisterSuccessful() throws AlreadyExists {
        User user = TestEntities.getDefaultUser1();
        RegisterUserDTO registerUserDTO = new RegisterUserDTO(user.getEmail(), user.getUsername(), "1234");
        when(authenticationService.signup(registerUserDTO)).thenReturn(user);

        ResponseEntity<User> response = authenticationController.register(registerUserDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }
    @Test
    void testRegisterFailed()throws AlreadyExists {
        User user = TestEntities.getDefaultUser1();
        RegisterUserDTO registerUserDTO = new RegisterUserDTO(user.getEmail(), user.getUsername(), "1234");
        when(authenticationService.signup(registerUserDTO)).thenThrow(AlreadyExists.class);

        assertThrows(AlreadyExists.class, () -> authenticationController.register(registerUserDTO));
    }
    @Test
    void testLogin(){
        User user = TestEntities.getDefaultUser1();
        LoginUserDTO loginUserDTO = new LoginUserDTO("john_doe", "1233");
        when(authenticationService.authenticate(loginUserDTO)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("");
        when(jwtService.getExpirationTime()).thenReturn(1L);

        ResponseEntity<LoginResponse> response = authenticationController.login(loginUserDTO);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, Objects.requireNonNull(response.getBody()).getExpiresIn());
    }
}
