package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.service.MailQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SendMessageConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SendMessageConfiguration.class);

    @Autowired
    private MailQueueService mailQueueService;

    @Scheduled(fixedRateString = "${CHECK_MESSAGES_FREQUENCY}")
    public void sendAnnouncementMessages() {
        logger.info("Checking for messages to send.");
        mailQueueService.queueAnnouncementMail();
    }
}
