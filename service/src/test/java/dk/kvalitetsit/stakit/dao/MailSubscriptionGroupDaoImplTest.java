package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class MailSubscriptionGroupDaoImplTest extends AbstractDaoTest {
    @Autowired
    private MailSubscriptionGroupDao mailSubscriptionGroupDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsert() {
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, UUID.randomUUID());
        var groupId = testDataHelper.createGroup("group name", UUID.randomUUID(), "group description");

        var input = MailSubscriptionGroupsEntity.createInstance(mailSubscriptionId, groupId);

        mailSubscriptionGroupDao.insert(input);
    }

    @Test
    public void testDeleteByEmail() {
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, UUID.randomUUID());
        var groupId = testDataHelper.createGroup("group name", UUID.randomUUID(), "group description");

        var input = MailSubscriptionGroupsEntity.createInstance(mailSubscriptionId, groupId);

        mailSubscriptionGroupDao.insert(input);

        mailSubscriptionGroupDao.deleteByEmail("email");
    }

    @Test
    public void testDeleteByUuid() {
        var uuid = UUID.randomUUID();
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, uuid);
        var groupId = testDataHelper.createGroup("group name", UUID.randomUUID(), "group description");

        var input = MailSubscriptionGroupsEntity.createInstance(mailSubscriptionId, groupId);

        mailSubscriptionGroupDao.insert(input);

        mailSubscriptionGroupDao.deleteByUuid(uuid);
    }
}
