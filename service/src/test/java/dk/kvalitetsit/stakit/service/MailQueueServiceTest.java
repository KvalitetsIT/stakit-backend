package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.times;

public class MailQueueServiceTest {
    private MailSubscriptionDao mailSubscriptionDao;
    private MailSenderService mailSenderService;
    private String templateSubject;
    private String templateBody;
    private ServiceConfigurationDao serviceConfigurationDao;
    private GroupConfigurationDao groupConfigurationDao;
    private ServiceStatusDao serviceStatusDao;
    private MailQueueServiceImpl mailQueue;

    @Before
    public void setup() {
        mailSubscriptionDao = Mockito.mock(MailSubscriptionDao.class);
        mailSenderService = Mockito.mock(MailSenderService.class);
        templateSubject = "subject group ${group_name}";
        templateBody = "Body ${service_name} - ${status} - ${status_time} - ${group_name}.";
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        groupConfigurationDao = Mockito.mock(GroupConfigurationDao.class);
        serviceStatusDao = Mockito.mock(ServiceStatusDao.class);

        mailQueue = new MailQueueServiceImpl(mailSubscriptionDao, mailSenderService, templateSubject, templateBody, serviceConfigurationDao, groupConfigurationDao, serviceStatusDao);
    }

    @Test
    public void testNoSubscriptions() {
        mailQueue.queueStatusUpdatedMail(1L, 2L);

        Mockito.verify(mailSubscriptionDao, times(1)).findSubscriptionsByServiceConfigurationId(1L);
    }

    @Test
    public void testSubscription() {
        var subscriptionOne = new MailSubscriptionEntity(1L, UUID.randomUUID(), "mailOne", true, true, UUID.randomUUID());
        var subscriptionTwo = new MailSubscriptionEntity(2L, UUID.randomUUID(), "mailTwo", true, true, UUID.randomUUID());

        var groupConfiguration = new GroupConfigurationEntity(10L, UUID.randomUUID(), "group name", 10);
        var serviceConfiguration = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", false, groupConfiguration.id());
        var serviceStatus = new ServiceStatusEntity(20L, serviceConfiguration.id(), "OK", OffsetDateTime.now(), null);

        Mockito.when(mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(1L)).thenReturn(Arrays.asList(subscriptionOne, subscriptionTwo));
        Mockito.when(serviceConfigurationDao.findById(1L)).thenReturn(Optional.of(serviceConfiguration));
        Mockito.when(groupConfigurationDao.findById(groupConfiguration.id())).thenReturn(Optional.of(groupConfiguration));
        Mockito.when(serviceStatusDao.findById(2L)).thenReturn(Optional.of(serviceStatus));

        mailQueue.queueStatusUpdatedMail(1L, 2L);

        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionOne.email()), Mockito.eq("subject group group name"), Mockito.eq("Body name - OK - %s - group name.".formatted(serviceStatus.statusTime())));
        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionTwo.email()), Mockito.eq("subject group group name"), Mockito.eq("Body name - OK - %s - group name.".formatted(serviceStatus.statusTime())));
        Mockito.verify(serviceConfigurationDao, times(1)).findById(1L);
        Mockito.verify(serviceStatusDao, times(1)).findById(2L);
        Mockito.verify(groupConfigurationDao, times(1)).findById(groupConfiguration.id());

        Mockito.verifyNoMoreInteractions(mailSenderService, serviceConfigurationDao, serviceStatusDao, groupConfigurationDao);
    }

    @Test
    public void testSubscriptionNoGroup() {
        var subscriptionOne = new MailSubscriptionEntity(1L, UUID.randomUUID(), "mailOne", true, true, UUID.randomUUID());

        var serviceConfiguration = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", false, null);
        var serviceStatus = new ServiceStatusEntity(20L, serviceConfiguration.id(), "OK", OffsetDateTime.now(), null);

        Mockito.when(mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(1L)).thenReturn(Collections.singletonList(subscriptionOne));
        Mockito.when(serviceConfigurationDao.findById(1L)).thenReturn(Optional.of(serviceConfiguration));
        Mockito.when(serviceStatusDao.findById(2L)).thenReturn(Optional.of(serviceStatus));

        mailQueue.queueStatusUpdatedMail(1L, 2L);

        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionOne.email()), Mockito.eq("subject group Default"), Mockito.eq("Body name - OK - %s - Default.".formatted(serviceStatus.statusTime())));
        Mockito.verify(serviceConfigurationDao, times(1)).findById(1L);
        Mockito.verify(serviceStatusDao, times(1)).findById(2L);

        Mockito.verifyNoMoreInteractions(mailSenderService, serviceConfigurationDao, serviceStatusDao);
        Mockito.verifyNoInteractions(groupConfigurationDao);
    }
}
