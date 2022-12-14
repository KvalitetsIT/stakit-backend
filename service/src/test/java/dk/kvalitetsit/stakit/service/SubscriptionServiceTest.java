package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.Subscription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

public class SubscriptionServiceTest {
    private SubscriptionServiceImpl subscriptionService;
    private GroupConfigurationDao groupConfigurationDao;
    private MailSubscriptionDao subscriptionDao;
    private MailSubscriptionGroupDao mailSubscriptionGroupDao;
    private MailSenderService mailSenderService;

    @Before
    public void setup() {
        groupConfigurationDao = Mockito.mock(GroupConfigurationDao.class);
        subscriptionDao = Mockito.mock(MailSubscriptionDao.class);
        mailSubscriptionGroupDao = Mockito.mock(MailSubscriptionGroupDao.class);
        mailSenderService = Mockito.mock(MailSenderService.class);

        subscriptionService = new SubscriptionServiceImpl(groupConfigurationDao, subscriptionDao, mailSubscriptionGroupDao, mailSenderService);
    }

    @Test
    public void testSubscribe() {
        var input = new Subscription("email", Arrays.asList(UUID.randomUUID(), UUID.randomUUID()), true);

        var groupOne = new GroupConfigurationEntity(1L, input.groups().get(0), "group one", 10);
        var groupTwo = new GroupConfigurationEntity(2L, input.groups().get(1), "group two", 10);

        Mockito.when(subscriptionDao.insert(Mockito.any())).thenReturn(10L);
        Mockito.when(groupConfigurationDao.findByUuid(input.groups().get(0))).thenReturn(Optional.of(groupOne));
        Mockito.when(groupConfigurationDao.findByUuid(input.groups().get(1))).thenReturn(Optional.of(groupTwo));

        var result = subscriptionService.subscribe(input);

        Mockito.verify(subscriptionDao, times(1)).insert(MailSubscriptionEntity.createInstance(result, input.email(), input.announcements(), false, Mockito.any()));
        Mockito.verify(mailSubscriptionGroupDao, times(1)).insert(MailSubscriptionGroupsEntity.createInstance(10L, 1L));
        Mockito.verify(mailSubscriptionGroupDao, times(1)).insert(MailSubscriptionGroupsEntity.createInstance(10L, 2L));
        Mockito.verify(mailSenderService, times(1)).sendMail(Mockito.eq(input.email()), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testSubscribeGroupNotFound() {
        var input = new Subscription("email", Arrays.asList(UUID.randomUUID(), UUID.randomUUID()), true);

        var groupOne = new GroupConfigurationEntity(1L, input.groups().get(0), "group one", 10);

        Mockito.when(subscriptionDao.insert(Mockito.any())).thenReturn(10L);
        Mockito.when(groupConfigurationDao.findByUuid(input.groups().get(0))).thenReturn(Optional.of(groupOne));
        Mockito.when(groupConfigurationDao.findByUuid(input.groups().get(1))).thenReturn(Optional.empty());

        var expectedException = assertThrows(InvalidDataException.class, () -> subscriptionService.subscribe(input));
        assertEquals("Group not found: %s".formatted(input.groups().get(1)), expectedException.getMessage());

        Mockito.verify(subscriptionDao, times(1)).insert(Mockito.any());
        Mockito.verify(mailSubscriptionGroupDao, times(1)).insert(Mockito.any());
        Mockito.verify(mailSubscriptionGroupDao, times(0)).insert(MailSubscriptionGroupsEntity.createInstance(10L, 2L));
        Mockito.verifyNoInteractions(mailSenderService);
    }

    @Test
    public void testSubscribeConfirm() {
        var input = UUID.randomUUID();

        Mockito.when(subscriptionDao.updateConfirmedByConfirmationUuid(Mockito.any())).thenReturn(true);

        subscriptionService.confirmSubscription(input);

        Mockito.verify(subscriptionDao, times(1)).updateConfirmedByConfirmationUuid(input);
    }
}
