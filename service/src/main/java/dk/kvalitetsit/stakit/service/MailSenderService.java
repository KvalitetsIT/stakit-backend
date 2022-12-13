package dk.kvalitetsit.stakit.service;

public interface MailSenderService {
    void sendMail(String to, String subject, String text);
}
