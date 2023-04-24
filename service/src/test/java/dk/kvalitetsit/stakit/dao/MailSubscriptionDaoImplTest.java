package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MailSubscriptionDaoImplTest extends AbstractDaoTest {
    @Autowired
    private MailSubscriptionDao mailSubscriptionDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsert() {
        var input = MailSubscriptionEntity.createInstance(UUID.randomUUID(), "email", true, true, UUID.randomUUID());

        mailSubscriptionDao.insert(input);
    }

    @Test
    public void testFindSubscriptionsByServiceConfigurationId() {
        var groupId = testDataHelper.createGroup("group name", UUID.randomUUID(), "group description");
        var serviceConfigurationId = testDataHelper.createServiceConfiguration("service", "service name", true, groupId, "OK", "description");
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, UUID.randomUUID(), UUID.randomUUID(), "email");
        testDataHelper.createMailSubscriptionGroup(mailSubscriptionId, groupId);

        var result = mailSubscriptionDao.findSubscriptionsByServiceConfigurationId(serviceConfigurationId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("email", result.get(0).email());
        assertTrue(result.get(0).confirmed());
        assertTrue(result.get(0).announcements());
    }

    @Test
    public void testUpdateConfirmedByConfirmationUuid() {
        var confirmationIdentifier = UUID.randomUUID();
        testDataHelper.createMailSubscription(false, confirmationIdentifier);

        var updated = mailSubscriptionDao.updateConfirmedByConfirmationUuid(confirmationIdentifier);
        assertTrue(updated);
    }

    @Test
    public void testUpdateConfirmedByConfirmationUuidNotFound() {
        var confirmationIdentifier = UUID.randomUUID();

        var updated = mailSubscriptionDao.updateConfirmedByConfirmationUuid(confirmationIdentifier);
        assertFalse(updated);
    }

    @Test
    public void testDeleteByEmail() {
        testDataHelper.createMailSubscription(true, UUID.randomUUID());

        mailSubscriptionDao.deleteByEmail("email");
    }

    @Test
    public void testDeleteByUuid() {
        var uuid = UUID.randomUUID();
        testDataHelper.createMailSubscription(true, UUID.randomUUID(), uuid);

        var result = mailSubscriptionDao.deleteByUuid(uuid);
        assertEquals(1, result);
    }

    @Test
    public void testFindAnnouncementSubscriptions() {
        var announcementsId = testDataHelper.createMailSubscription(true, UUID.randomUUID(), UUID.randomUUID(), true, "email1");
        testDataHelper.createMailSubscription(true, UUID.randomUUID(), UUID.randomUUID(), false, "email2");

        var result = mailSubscriptionDao.findAnnouncementSubscriptions();

        assertEquals(1, result.size());
        assertEquals(announcementsId, result.get(0).id());
    }
}
