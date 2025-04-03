package org.arthub.backend.controller;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.dto.PasswordResetRequestDTO;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.exception.TokenIsNotValid;
import org.arthub.backend.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;


@SpringBootTest
class PasswordResetControllerTest {
    @MockBean
    private PasswordResetService passwordResetService;
    @Autowired
    private PasswordResetController passwordResetController;
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testResetPasswordRequest() throws NotFound {
        String email = "test@test.com";
        doNothing().when(passwordResetService).resetPasswordRequest(email);
        ResponseEntity<String> response = passwordResetController.resetPasswordRequest(email);
        assertEquals(200, response.getStatusCode().value());
    }
    @Test
    void testResetPassword() throws NotFound, TokenIsNotValid, ConfirmPasswordIsNotEqual {
        String email = "test@test.com";
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(email, email, email);
        doNothing().when(passwordResetService).resetPassword(requestDTO.getToken(), requestDTO.getPassword(), requestDTO.getPassword());
        ResponseEntity<String> response = passwordResetController.resetPassword(requestDTO);
        assertEquals(200, response.getStatusCode().value());
    }
}
