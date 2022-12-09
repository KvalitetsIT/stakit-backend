package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class MailSubscriptionGroupDaoTest extends AbstractDaoTest {
    @Autowired
    private MailSubscriptionGroupDao mailSubscriptionGroupDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsert() {
        var mailSubscriptionId = testDataHelper.createMailSubscription();
        var groupId = testDataHelper.createGroup("group name", UUID.randomUUID());

        var input = MailSubscriptionGroupsEntity.createInstance(mailSubscriptionId, groupId);

        mailSubscriptionGroupDao.insert(input);
    }
}
