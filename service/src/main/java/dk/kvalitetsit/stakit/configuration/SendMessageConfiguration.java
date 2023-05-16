package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.service.MailQueueService;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT10M")
public class SendMessageConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SendMessageConfiguration.class);

    @Autowired
    private MailQueueService mailQueueService;

    @SchedulerLock(name = "mailQueue")
    @Scheduled(fixedDelayString = "${CHECK_MESSAGES_FREQUENCY}")
    public void sendAnnouncementMessages() {
        LockAssert.assertLocked();

        logger.info("Checking for messages to send.");
        mailQueueService.queueAnnouncementMail();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }
}
