package org.arthub.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arthub.backend.exception.EmailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * This service is responsible for sending emails with customizable recipients, senders, subjects, and content.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /**
     * JavaMailSender instance used to send emails.
     */
    private final JavaMailSender mailSender;

    /**
     * Sends an email with the specified parameters.
     *
     * @param to      the recipient's email address
     * @param from    the sender's email address
     * @param subject the subject of the email
     * @param text    the content of the email
     * @param html    whether the email content is in HTML format
     * @throws EmailSendException if an error occurs while sending the email
     */
    public void sendEmail(final String to,
                          final String from,
                          final String subject,
                          final String text,
                          final Boolean html
    ) throws EmailSendException {
        log.info("Attempting to send email to: {} with subject: {}", to, subject);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            helper.setFrom(from);

            mailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email", e);
        }
    }


}
