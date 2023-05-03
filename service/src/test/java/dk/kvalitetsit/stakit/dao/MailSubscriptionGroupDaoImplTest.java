package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void testGetAllSubscriptions() {
        ArrayList<SubscriptionGroupEntity> expected = new ArrayList<>();

        IntStream.range(1, 10).forEach(i -> {
            var uuid = UUID.randomUUID();
            var groupUuid = UUID.randomUUID();
            var email = String.format("email%s", i);
            var mailSubscriptionId = testDataHelper.createMailSubscription(true, uuid, uuid, email);
            var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");

            testDataHelper.createMailSubscriptionGroup(mailSubscriptionId, groupId);
            expected.add(SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid));
        });

        List<SubscriptionGroupEntity> response = mailSubscriptionGroupDao.getSubscriptions();
        assertEquals(expected, response);
    }

    @Test
    public void testGetSubscriptionByUuid() {

        var uuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var email = "email";
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, uuid, uuid, email);
        var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");

        testDataHelper.createMailSubscriptionGroup(mailSubscriptionId, groupId);

        var expectedResponse = SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid);

        SubscriptionGroupEntity response =  mailSubscriptionGroupDao.getSubscriptionByUuid(uuid);

        assertEquals(expectedResponse, response, "Expected the same response as were initially added");
    }

    @Test
    public void testGetSubscriptionByUuidReturnsAllGroups(){
        var uuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var email = "email";
        var mailSubscriptionId = testDataHelper.createMailSubscription(true, uuid, uuid, email);
        var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");


        List<SubscriptionGroupEntity> expected = IntStream.range(0, 2).mapToObj(i -> {
            testDataHelper.createMailSubscriptionGroup(mailSubscriptionId, groupId);
            return SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid);
        }).toList();

        List<SubscriptionGroupEntity> response =  mailSubscriptionGroupDao.getSubscriptionByUuid(uuid);

        assertEquals(expected, response, "Expected the same response as were initially added");
    }
}

