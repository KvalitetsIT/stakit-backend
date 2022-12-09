package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class MailSubscriptionDaoTest extends AbstractDaoTest {
    @Autowired
    private MailSubscriptionDao mailSubscriptionDao;

    @Test
    public void testInsert() {
        var input = MailSubscriptionEntity.createInstance(UUID.randomUUID(), "email", true, true, UUID.randomUUID());

        mailSubscriptionDao.insert(input);
    }
}
