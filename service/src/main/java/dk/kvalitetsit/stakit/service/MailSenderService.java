package dk.kvalitetsit.stakit.service;

import javax.mail.MessagingException;

public interface MailSenderService {
    void sendMailAsync(String to, String subject, String text);
    void sendMail(String to, String subject, String text) throws MessagingException;
}
