package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionDao;
import dk.kvalitetsit.stakit.dao.MailSubscriptionGroupDao;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionManagementServiceImplTest {

    private MailSubscriptionGroupDao mock;
    private SubscriptionManagementServiceImpl subject;

    @BeforeEach
    public void setup() {
        mock = Mockito.mock(MailSubscriptionGroupDao.class);
        subject = new SubscriptionManagementServiceImpl(mock);
    }
    @Test
    void getSubscriptions() {

        List<SubscriptionModel> expected = new LinkedList<>();

        List<SubscriptionGroupEntity> response = IntStream.range(1, 3).mapToObj(i -> {
            var uuid = UUID.randomUUID();
            var email = String.format("email%s", i);
            var groupUuid = UUID.randomUUID();

            expected.add(new SubscriptionModel(uuid, email, List.of(groupUuid), true));

            return SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid);
        }).collect(Collectors.toList());

        Mockito.when(mock.getSubscriptions()).thenReturn(response);

        assertEquals(expected, subject.getSubscriptions());
    }

    @Test
    void getSubscription() {
        var uuid = UUID.randomUUID();
        var email = "email";
        var groupUuid = UUID.randomUUID();

        SubscriptionModel expected = new SubscriptionModel(uuid, email, List.of(groupUuid), true);

        SubscriptionGroupEntity response = SubscriptionGroupEntity.createInstance(uuid, email, true, groupUuid);

        Mockito.when(mock.getSubscriptionByUuid(uuid)).thenReturn(response);

        assertEquals(Optional.of(expected), subject.getSubscription(uuid));


    }
}