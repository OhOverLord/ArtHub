package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.arthub.backend.entity.PasswordResetToken;
import org.arthub.backend.entity.User;
import org.arthub.backend.exception.ConfirmPasswordIsNotEqual;
import org.arthub.backend.exception.NotFound;
import org.arthub.backend.exception.TokenIsNotValid;
import org.arthub.backend.mock.TestEntities;
import org.arthub.backend.repository.PasswordResetTokenRepository;
import org.arthub.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PasswordResetServiceTest {
    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;

    @Test
    void testResetPasswordRequestFailure(){
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(NotFound.class, () -> passwordResetService.resetPasswordRequest(email));
    }

    @Test
    void testResetPasswordRequestSuccess() throws NotFound{
        String email = "test@test.com";
        User user = TestEntities.getDefaultUser1();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenReturn(null);
        doNothing().when(emailService).sendEmail(any(), any(), any(), any(), any());

        passwordResetService.resetPasswordRequest(email);
        verify(emailService, times(1)).sendEmail(any(), any(), any(), any(), any());
    }

    @Test
    void testResetPasswordSuccess() throws NotFound, TokenIsNotValid, ConfirmPasswordIsNotEqual {
        User user = TestEntities.getDefaultUser1();
        PasswordResetToken resetToken = TestEntities.getResetToken();
        String password = "sword_fish";
        resetToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));
        when(userRepository.save(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(resetToken)).thenReturn(null);

        passwordResetService.resetPassword(resetToken.getToken(), password, password);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testResetPasswordFailureTokenNotFound() {
        User user = TestEntities.getDefaultUser1();
        PasswordResetToken resetToken = TestEntities.getResetToken();
        String password = "sword_fish";
        resetToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(password)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> passwordResetService.resetPassword(resetToken.getToken(), password, password));
    }
    @Test
    void testResetPasswordFailureTokenIsNotValid() {
        User user = TestEntities.getDefaultUser1();
        PasswordResetToken resetToken = TestEntities.getResetToken();
        String password = "sword_fish";
        resetToken.setUser(user);

        resetToken.setIsUsed(true);
        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));


        assertThrows(TokenIsNotValid.class, () -> passwordResetService.resetPassword(resetToken.getToken(), password, password));
    }
    @Test
    void testResetPasswordFailurePasswordsAreNotEqual() {
        User user = TestEntities.getDefaultUser1();
        PasswordResetToken resetToken = TestEntities.getResetToken();
        String password = "sword_fish";
        resetToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        assertThrows(ConfirmPasswordIsNotEqual.class, () -> passwordResetService.resetPassword(resetToken.getToken(), resetToken.getToken(), password));
    }
}
