package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriptionMapper {
    public static MailSubscriptionEntity mapSubscription(SubscriptionModel mapSubscriptionModel) {
        return MailSubscriptionEntity.createInstance(UUID.randomUUID(), mapSubscriptionModel.email(), mapSubscriptionModel.announcements(), false, UUID.randomUUID());
    }

    public  static SubscriptionModel mapSubscription(SubscriptionGroupEntity sub) {
        return new SubscriptionModel(sub.subUuid(), sub.email(), sub.groupUuid() == null ? new ArrayList<>() : List.of(sub.groupUuid()), sub.announcements());
    }
}
