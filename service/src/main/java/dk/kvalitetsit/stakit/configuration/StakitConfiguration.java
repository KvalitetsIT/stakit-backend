package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.controller.interceptor.ApiAccessInterceptor;
import dk.kvalitetsit.stakit.dao.*;
import dk.kvalitetsit.stakit.service.*;
import dk.kvalitetsit.stakit.session.JwtTokenParser;
import dk.kvalitetsit.stakit.session.UserContextService;
import dk.kvalitetsit.stakit.session.UserContextServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Configuration
public class StakitConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(StakitConfiguration.class);
    private ApiAccessInterceptor apiAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAccessInterceptor);
    }

    @Value("${ALLOWED_ORIGINS}")
    private List<String> allowedOrigins;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        allowedOrigins.forEach(config::addAllowedOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);

        return bean;
    }

    @Bean
    public ApiAccessInterceptor apiAccessInterceptor(UserContextService userContextService, @Value("${ADAPTER_API_KEY}") String apiKey) {
        apiAccessInterceptor = new ApiAccessInterceptor(userContextService, apiKey);

        return apiAccessInterceptor;
    }

    @Bean
    public StatusUpdateService statusUpdateService(ServiceConfigurationDao serviceConfigurationDao,
                                                   ServiceStatusDao serviceStatusDao,
                                                   MailQueueService mailQueueService,
                                                   GroupConfigurationDao groupConfigurationDao) {
        return new StatusUpdateServiceImpl(serviceConfigurationDao, serviceStatusDao, mailQueueService, groupConfigurationDao);
    }

    @Bean
    public StatusGroupService statusGroupService(GroupedStatusDao groupedStatusDao) {
        return new StatusGroupServiceImpl(groupedStatusDao);
    }

    @Bean
    public GroupService groupService(GroupConfigurationDao groupConfigurationDao, ServiceConfigurationDao serviceConfigurationDao, MailSubscriptionGroupDao mailSubscriptionGroupDao) {
        return new GroupServiceImpl(groupConfigurationDao, serviceConfigurationDao, mailSubscriptionGroupDao);
    }

    @Bean
    public ServiceManagementService serviceManagementService(ServiceConfigurationDao serviceConfigurationDao,
                                                             GroupConfigurationDao groupConfigurationDao,
                                                             ServiceStatusDao serviceStatusDao) {
        return new ServiceManagementServiceImpl(serviceConfigurationDao, groupConfigurationDao, serviceStatusDao);
    }

    @Bean
    public AnnouncementService announcementService(AnnouncementDao announcementDao) {
        return new AnnouncementServiceImpl(announcementDao);
    }

    @Bean
    public MailSenderService mailSenderService(JavaMailSender javaMailSender,
                                               @Value("${MAIL_FROM}") String from) {
        return new MailSenderServiceImpl(javaMailSender, from);
    }

    @Bean
    public SubscriptionService subscriptionService(GroupConfigurationDao groupConfigurationDao,
                                                   MailSubscriptionDao subscriptionDao,
                                                   MailSubscriptionGroupDao subscriptionGroupDao,
                                                   MailSenderService mailSenderService,
                                                   @Value("${BASE_URL}") String baseUrl) {
        return new SubscriptionServiceImpl(groupConfigurationDao, subscriptionDao, subscriptionGroupDao, mailSenderService, baseUrl);
    }

    @Bean
    public SubscriptionManagementService subscriptionManagementService(MailSubscriptionGroupDao mailSubscriptionGroupDao) {
        return new SubscriptionManagementServiceImpl(mailSubscriptionGroupDao);
    }


    @Bean
    public MailQueueService mailService(MailSubscriptionDao mailSubscriptionDao,
                                        MailSenderService mailSenderService,
                                        ServiceConfigurationDao serviceConfigurationDao,
                                        GroupConfigurationDao groupConfigurationDao,
                                        ServiceStatusDao serviceStatusDao,
                                        @Value("${STATUS_UPDATE_SUBJECT_TEMPLATE}") String templateSubject,
                                        @Value("${STATUS_UPDATE_BODY_TEMPLATE}") String templateBody,
                                        @Value("${BASE_URL}") String baseUrl,
                                        AnnouncementDao announcementDao) {

        return new MailQueueServiceImpl(mailSubscriptionDao, mailSenderService, templateSubject, templateBody, serviceConfigurationDao, groupConfigurationDao, serviceStatusDao, baseUrl, announcementDao);
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
    public JwtTokenParser jwtTokenParser(@Value("${JWT_SIGNING_KEY}") String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return new JwtTokenParser(publicKey);
    }

    @Bean
    @RequestScope
    public UserContextService userContextService(HttpServletRequest request, JwtTokenParser tokenParser) {
        return new UserContextServiceImpl(request, tokenParser);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
