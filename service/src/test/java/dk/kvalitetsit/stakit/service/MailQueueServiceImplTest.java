package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.*;
import dk.kvalitetsit.stakit.dao.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.Mockito.times;

public class MailQueueServiceImplTest {
    private MailSubscriptionDao mailSubscriptionDao;
    private MailSenderService mailSenderService;
    private String templateSubject;
    private String templateBody;
    private ServiceConfigurationDao serviceConfigurationDao;
    private GroupConfigurationDao groupConfigurationDao;
    private ServiceStatusDao serviceStatusDao;
    private MailQueueServiceImpl mailQueue;
    private String baseUrl;
    private AnnouncementDao announcementDao;

    @Before
    public void setup() {
        mailSubscriptionDao = Mockito.mock(MailSubscriptionDao.class);
        mailSenderService = Mockito.mock(MailSenderService.class);
        templateSubject = "subject group ${group_name}";
        templateBody = "Body ${service_name} - ${status} - ${status_time} - ${group_name} - ${unsubscribe_url}.";
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        groupConfigurationDao = Mockito.mock(GroupConfigurationDao.class);
        serviceStatusDao = Mockito.mock(ServiceStatusDao.class);
        baseUrl = "base_url";
        announcementDao = Mockito.mock(AnnouncementDao.class);

        mailQueue = new MailQueueServiceImpl(
                mailSubscriptionDao,
                mailSenderService,
                templateSubject,
                templateBody,
                serviceConfigurationDao,
                groupConfigurationDao,
                serviceStatusDao,
                baseUrl,
                announcementDao
        );
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

        var groupConfiguration = new GroupConfigurationEntity(10L, UUID.randomUUID(), "group name", 10, "description", true, true);
        var serviceConfiguration = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", false, groupConfiguration.id(), "OK", "description");
        var serviceStatus = new ServiceStatusEntity(20L, serviceConfiguration.id(), "OK", OffsetDateTime.now(), null);

        Mockito.when(mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(1L)).thenReturn(Arrays.asList(subscriptionOne, subscriptionTwo));
        Mockito.when(serviceConfigurationDao.findById(1L)).thenReturn(Optional.of(serviceConfiguration));
        Mockito.when(groupConfigurationDao.findById(groupConfiguration.id())).thenReturn(Optional.of(groupConfiguration));
        Mockito.when(serviceStatusDao.findById(2L)).thenReturn(Optional.of(serviceStatus));

        mailQueue.queueStatusUpdatedMail(1L, 2L);

        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionOne.email()), Mockito.eq("subject group group name"), Mockito.eq("Body name - OK - %s - group name - base_url/unsubscribe/%s.".formatted(serviceStatus.statusTime(), subscriptionOne.uuid())));
        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionTwo.email()), Mockito.eq("subject group group name"), Mockito.eq("Body name - OK - %s - group name - base_url/unsubscribe/%s.".formatted(serviceStatus.statusTime(), subscriptionTwo.uuid())));
        Mockito.verify(serviceConfigurationDao, times(1)).findById(1L);
        Mockito.verify(serviceStatusDao, times(1)).findById(2L);
        Mockito.verify(groupConfigurationDao, times(1)).findById(groupConfiguration.id());

        Mockito.verifyNoMoreInteractions(mailSenderService, serviceConfigurationDao, serviceStatusDao, groupConfigurationDao);
    }

    @Test
    public void testSubscriptionNoGroup() {
        var subscriptionOne = new MailSubscriptionEntity(1L, UUID.randomUUID(), "mailOne", true, true, UUID.randomUUID());

        var serviceConfiguration = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", false, null, "OK","description");
        var serviceStatus = new ServiceStatusEntity(20L, serviceConfiguration.id(), "OK", OffsetDateTime.now(), null);

        Mockito.when(mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(1L)).thenReturn(Collections.singletonList(subscriptionOne));
        Mockito.when(serviceConfigurationDao.findById(1L)).thenReturn(Optional.of(serviceConfiguration));
        Mockito.when(serviceStatusDao.findById(2L)).thenReturn(Optional.of(serviceStatus));

        mailQueue.queueStatusUpdatedMail(1L, 2L);

        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionOne.email()), Mockito.eq("subject group Default"), Mockito.eq("Body name - OK - %s - Default - base_url/unsubscribe/%s.".formatted(serviceStatus.statusTime(), subscriptionOne.uuid())));
        Mockito.verify(serviceConfigurationDao, times(1)).findById(1L);
        Mockito.verify(serviceStatusDao, times(1)).findById(2L);

        Mockito.verifyNoMoreInteractions(mailSenderService, serviceConfigurationDao, serviceStatusDao);
        Mockito.verifyNoInteractions(groupConfigurationDao);
    }

    @Test
    public void testAnnouncementNoSubscriptions() {
        mailQueue.queueAnnouncementMail();

        Mockito.verify(mailSubscriptionDao, times(1)).findAnnouncementSubscriptions();
        Mockito.verifyNoInteractions(mailSenderService);
    }

    @Test
    public void testAnnouncement() {
        var subscriptionOne = new MailSubscriptionEntity(1L, UUID.randomUUID(), "mailOne", true, true, UUID.randomUUID());
        var subscriptionTwo = new MailSubscriptionEntity(2L, UUID.randomUUID(), "mailTwo", true, true, UUID.randomUUID());
        Mockito.when(mailSubscriptionDao.findAnnouncementSubscriptions()).thenReturn(List.of(subscriptionOne, subscriptionTwo));

        var announcement = new AnnouncementEntity(10L, UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject", "message");
        List<AnnouncementEntity> announcementList = new ArrayList<>();
        announcementList.add(announcement);
        Mockito.when(announcementDao.getAnnouncementsToSend()).thenReturn(announcementList);

        mailQueue.queueAnnouncementMail();

        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionOne.email()), Mockito.eq("Stakit Announcement"), Mockito.eq("message\n" +
                "\n" +
                "base_url/unsubscribe/%s".formatted(subscriptionOne.uuid())));
        Mockito.verify(mailSenderService, times(1)).sendMailAsync(Mockito.eq(subscriptionTwo.email()), Mockito.eq("Stakit Announcement"), Mockito.eq("message\n" +
                "\n" +
                "base_url/unsubscribe/%s".formatted(subscriptionTwo.uuid())));
        Mockito.verify(announcementDao, times(1)).getAnnouncementsToSend();
        Mockito.verify(mailSubscriptionDao, times(1)).findAnnouncementSubscriptions();
        Mockito.verify(announcementDao, times(1)).updateAnnouncementToSent(announcement);

        Mockito.verifyNoMoreInteractions(mailSenderService, serviceConfigurationDao, serviceStatusDao, groupConfigurationDao);
    }
}
