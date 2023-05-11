package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionManagementServiceImplTest {

    private MailSubscriptionGroupDao mailSubscriptionGroupDao;
    private SubscriptionManagementServiceImpl subject;

    @BeforeEach
    public void setup() {
        mailSubscriptionGroupDao = Mockito.mock(MailSubscriptionGroupDao.class);
        subject = new SubscriptionManagementServiceImpl(mailSubscriptionGroupDao);
    }
    @Test
    void getSubscriptions() {
        List<SubscriptionModel> expected = new LinkedList<>();
        List<SubscriptionGroupEntity> response = new LinkedList<>();

        for ( int i = 0 ; i < 10; i++){
            var uuid = UUID.randomUUID();
            var email = String.format("email%s", i);
            var groupUuid = UUID.randomUUID();

            expected.add(new SubscriptionModel(uuid, email, List.of(groupUuid), true));
            response.add(SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid));
        }

        Mockito.when(mailSubscriptionGroupDao.getSubscriptions()).thenReturn(response);
        assertEquals(expected, subject.getSubscriptions());
    }

    @Test
    public void testGetSubscriptionsNoGroups() {
        var subscription = new SubscriptionGroupEntity(UUID.randomUUID(), "email", true, null);
        Mockito.when(mailSubscriptionGroupDao.getSubscriptions()).thenReturn(Collections.singletonList(subscription));

        var result = subject.getSubscriptions();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).groups().isEmpty());
        assertEquals(subscription.subUuid(), result.get(0).uuid());
        assertEquals(subscription.email(), result.get(0).email());
        assertTrue(result.get(0).announcements());
    }

    @Test
    void givenSubsThatSharesUuidWhenGetSubscriptionsThenMerge() {

        List<SubscriptionModel> expected = new LinkedList<>();
        List<SubscriptionGroupEntity> response = new LinkedList<>();

        var uuid = UUID.randomUUID();

        for ( int i = 0 ; i < 10; i++){
            var email = String.format("email%s", i);
            var groupUuid = UUID.randomUUID();

            expected.add(new SubscriptionModel(uuid, email, List.of(groupUuid), true));
            response.add(SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid));
        }
        Mockito.when(mailSubscriptionGroupDao.getSubscriptions()).thenReturn(response);
        assertEquals(1, subject.getSubscriptions().size(), "Expected only a single response as every response by the dao shares the same uuid");
    }


    @Test
    void getSubscriptionWithGroup() {
        var uuid = UUID.randomUUID();
        var email = "email";
        var groupUuid = UUID.randomUUID();

        SubscriptionModel expected = new SubscriptionModel(uuid, email, List.of(groupUuid), true);

        SubscriptionGroupEntity response = SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid);

        Mockito.when(mailSubscriptionGroupDao.getSubscriptionByUuid(uuid)).thenReturn(List.of(response));

        assertEquals(Optional.of(expected), subject.getSubscription(uuid));
    }

    @Test
    void getSubscriptionWithoutGroup() {
        var uuid = UUID.randomUUID();
        var email = "email";

        SubscriptionGroupEntity databaseSubscriptionGroupEntity = SubscriptionGroupEntity.createInstance(uuid, email, true, null);

        Mockito.when(mailSubscriptionGroupDao.getSubscriptionByUuid(uuid)).thenReturn(List.of(databaseSubscriptionGroupEntity));

        var result = subject.getSubscription(uuid);

        assertTrue(result.isPresent());
        assertTrue(result.get().announcements());
        assertEquals(email, result.get().email());
        assertEquals(uuid, result.get().uuid());
        assertTrue(result.get().groups().isEmpty());
    }

    @Test
    void getSubscriptionWithMultipleGroups() {
        var uuid = UUID.randomUUID();
        var email = "email";

        var groupOne = UUID.randomUUID();
        var groupTwo = UUID.randomUUID();

        SubscriptionGroupEntity databaseSubscriptionGroupEntityOne = SubscriptionGroupEntity.createInstance(uuid, email, true, groupOne);
        SubscriptionGroupEntity databaseSubscriptionGroupEntityTwo = SubscriptionGroupEntity.createInstance(uuid, email, true, groupTwo);

        Mockito.when(mailSubscriptionGroupDao.getSubscriptionByUuid(uuid)).thenReturn(Arrays.asList(databaseSubscriptionGroupEntityOne, databaseSubscriptionGroupEntityTwo));

        var result = subject.getSubscription(uuid);

        assertTrue(result.isPresent());
        assertTrue(result.get().announcements());
        assertEquals(email, result.get().email());
        assertEquals(uuid, result.get().uuid());
        assertEquals(2, result.get().groups().size());
        assertEquals(List.of(groupOne, groupTwo), result.get().groups());
    }
}