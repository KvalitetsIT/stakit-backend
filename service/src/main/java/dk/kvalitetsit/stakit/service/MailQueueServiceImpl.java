package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.MessageModel;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MailQueueServiceImpl implements MailQueueService {
    private final MailSubscriptionDao mailSubscriptionDao;
    private final MailSenderService mailSenderService;
    private final String templateSubject;
    private final String templateBody;
    private final ServiceConfigurationDao serviceConfigurationDao;
    private final GroupConfigurationDao groupConfigurationDao;
    private final ServiceStatusDao serviceStatusDao;
    private final String baseUrl;


    public MailQueueServiceImpl(MailSubscriptionDao mailSubscriptionDao,
                                MailSenderService mailSenderService,
                                String templateSubject,
                                String templateBody,
                                ServiceConfigurationDao serviceConfigurationDao,
                                GroupConfigurationDao groupConfigurationDao,
                                ServiceStatusDao serviceStatusDao,
                                String baseUrl) {
        this.mailSubscriptionDao = mailSubscriptionDao;
        this.mailSenderService = mailSenderService;
        this.templateSubject = templateSubject;
        this.templateBody = templateBody;
        this.serviceConfigurationDao = serviceConfigurationDao;
        this.groupConfigurationDao = groupConfigurationDao;
        this.serviceStatusDao = serviceStatusDao;
        this.baseUrl = baseUrl;
    }

    @Override
    @Transactional
    public void queueStatusUpdatedMail(long serviceConfigurationId, long serviceStatusId) {
        var mails = mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(serviceConfigurationId);

        if(mails.isEmpty()) {
            return;
        }

        var serviceConfiguration = serviceConfigurationDao.findById(serviceConfigurationId).orElseThrow(() -> new RuntimeException("Service configuration not found. This should not happen. ID: " + serviceConfigurationId));
        var groupName = Optional.ofNullable(serviceConfiguration.groupConfigurationId()).flatMap(groupConfigurationDao::findById).map(GroupConfigurationEntity::name).orElse("Default");
        var serviceStatus = serviceStatusDao.findById(serviceStatusId).orElseThrow(() -> new RuntimeException("Service status not found. This should not happen. ID: " + serviceStatusId));

        mails.stream()
                .map(x -> new MessageModel(x.email(),
                                      substitute(templateSubject, serviceConfiguration, groupName, serviceStatus, x.uuid()),
                                      substitute(templateBody, serviceConfiguration, groupName, serviceStatus, x.uuid())))
                .forEach(this::processMail);
    }

    private void processMail(MessageModel messageModel) {
        mailSenderService.sendMailAsync(messageModel.to(), messageModel.subject(), messageModel.text());
    }

    private String substitute(String text, ServiceConfigurationEntity serviceConfiguration, String groupName, ServiceStatusEntity serviceStatus, UUID mailUuid) {
        var map = new HashMap<String, Object>();
        map.put("group_name", groupName);
        map.put("service_name", serviceConfiguration.name());
        map.put("status", serviceStatus.status());
        map.put("status_time", serviceStatus.statusTime());
        map.put("unsubscribe_url", baseUrl + "/unsubscribe/" + mailUuid);

        return StringSubstitutor.replace(text, map);
    }

}
