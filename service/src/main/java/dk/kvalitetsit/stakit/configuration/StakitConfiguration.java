package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.*;
import dk.kvalitetsit.stakit.service.*;
import dk.kvalitetsit.stakit.controller.interceptor.ApiAccessInterceptor;
import dk.kvalitetsit.stakit.session.UserContextService;
import dk.kvalitetsit.stakit.session.UserContextServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class StakitConfiguration implements WebMvcConfigurer {
    private ApiAccessInterceptor apiAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAccessInterceptor);
    }

    @Bean
    public ApiAccessInterceptor apiAccessInterceptor(UserContextService userContextService) {
        apiAccessInterceptor = new ApiAccessInterceptor(userContextService);

        return apiAccessInterceptor;
    }

    @Bean
    public StatusUpdateService statusUpdateService(ServiceConfigurationDao serviceConfigurationDao,
                                                   ServiceStatusDao serviceStatusDao,
                                                   MailService mailService) {
        return new StatusUpdateServiceImpl(serviceConfigurationDao, serviceStatusDao, mailService);
    }

    @Bean
    public StatusGroupService statusGroupService(GroupedStatusDao groupedStatusDao) {
        return new StatusGroupServiceImpl(groupedStatusDao);
    }

    @Bean
    public GroupService groupService(GroupConfigurationDao groupConfigurationDao) {
        return new GroupServiceImpl(groupConfigurationDao);
    }

    @Bean
    public ServiceManagementService serviceManagementService(ServiceConfigurationDao serviceConfigurationDao, GroupConfigurationDao groupConfigurationDao) {
        return new ServiceManagementServiceImpl(serviceConfigurationDao, groupConfigurationDao);
    }

    @Bean
    public AnnouncementService announcementService(AnnouncementDao announcementDao) {
        return new AnnouncementServiceImpl(announcementDao);
    }

    @Bean
    public MailSenderService mailSenderService(JavaMailSender javaMailSender) {
        return new MailSenderServiceImpl(javaMailSender, "sender_email");
    }

    @Bean
    public MailService mailService(MailSubscriptionDao mailSubscriptionDao, MailSenderService mailSenderService) {
        return new MailServiceImpl(mailSubscriptionDao, mailSenderService);
    }

    @Bean
    public JavaMailSender javaMailSender(@Value("${MAIL_HOST}") String host,
                                         @Value("${MAIL_PORT:587}") int port,
                                         @Value("${MAIL_USER}") String username,
                                         @Value("${MAIL_PASSWORD}") String password) {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    @RequestScope
    public UserContextService userContextService(HttpServletRequest request) {
        return new UserContextServiceImpl(request);
    }
}
