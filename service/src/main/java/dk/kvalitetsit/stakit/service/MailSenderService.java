package dk.kvalitetsit.stakit.service;

public interface MailSenderService {
    void sendMailAsync(String to, String subject, String text);
    void sendMail(String to, String subject, String text);
}
