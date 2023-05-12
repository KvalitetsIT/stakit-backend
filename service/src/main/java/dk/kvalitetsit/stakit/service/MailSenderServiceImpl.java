package dk.kvalitetsit.stakit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
        catch(MailException | MessagingException e) {
            // Empty
        }
    }

    public void sendMail(String to, String subject, String text) throws MessagingException {
        String htmlText = text.replace("\n", "<br>");
        try {
            MimeMessage mailMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            emailSender.send(mailMessage);
        } catch(MailException | MessagingException e) {
            logger.warn("Could not send mail.", e);
            throw e;
        }
    }
}
