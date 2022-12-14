package dk.kvalitetsit.stakit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public class MailSenderServiceImpl implements MailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(MailSenderServiceImpl.class);
    private final JavaMailSender emailSender;
    private final String from;

    public MailSenderServiceImpl(JavaMailSender emailSender, String from) {
        this.emailSender = emailSender;
        this.from = from;
    }

    @Override
    @Async
    public void sendMailAsync(String to, String subject, String text) {
        try {
            sendMail(to, subject, text);
        }
        catch(MailException e) {
            // Empty
        }
    }

    public void sendMail(String to, String subject, String text) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);;
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        try {
            emailSender.send(mailMessage);
        } catch(MailException e) {
            logger.warn("Could not send mail.", e);
            throw e;
        }
    }
}
