package org.arthub.backend.service;

import org.arthub.backend.client.AssociationServiceClient;
import org.arthub.backend.elasticsearch.PostElasticsearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceTest {
    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private MimeMessage mimeMessage;

    @Autowired
    private EmailService emailService;

    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
    @MockBean
    private AssociationServiceClient associationServiceClient;


    @Test
    void testSendEmailSuccessfully() {
        String to = "recipient@example.com";
        String from = "sender@example.com";
        String subject = "Test Subject";
        String text = "Test Email Content";
        Boolean isHtml = true;
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(to, from, subject, text, isHtml);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendEmailFailure() {
        String to = "recipient@example.com";
        String from = "sender@example.com";
        String subject = "Test Subject";
        String text = "Test Email Content";
        Boolean isHtml = true;
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Test exception"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThrows(MailSendException.class, () -> emailService.sendEmail(to, from, subject, text, isHtml));

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
