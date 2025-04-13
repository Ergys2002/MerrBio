package com.app.merrbioapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@merrbio.com}")
    private String fromEmail;

    @Async
    public void sendChatNotification(String toEmail, String senderName, String messagePreview, String conversationTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Unread message from " + senderName + " on MerrBio");
            
            String htmlContent = 
                    "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px;'>" +
                    "<h2 style='color: #3a873a;'>You have an unread message on MerrBio</h2>" +
                    "<p><strong>" + senderName + "</strong> has sent you a message in <em>" + conversationTitle + "</em> that you haven't read yet:</p>" +
                    "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                    "<p style='margin: 0; color: #555;'><em>\"" + messagePreview + "\"</em></p>" +
                    "</div>" +
                    "<p>Login to MerrBio to continue the conversation.</p>" +
                    "<a href='https://merrbio.com/messages' style='display: inline-block; " +
                    "background-color: #3a873a; color: white; padding: 10px 20px; " +
                    "text-decoration: none; border-radius: 5px; margin-top: 15px;'>" +
                    "View Message</a>" +
                    "<p style='margin-top: 30px; font-size: 12px; color: #888;'>This is an automated message, please do not reply directly to this email.</p>" +
                    "</div>";
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Chat notification email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send chat notification email to {}: {}", toEmail, e.getMessage());
        }
    }
}