package dk.kvalitetsit.stakit.service.mapper;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import dk.kvalitetsit.stakit.service.model.Subscription;

import java.util.UUID;

public class SubscriptionMapper {
    public static MailSubscriptionEntity mapSubscription(Subscription mapSubscription) {
        return MailSubscriptionEntity.createInstance(UUID.randomUUID(), mapSubscription.email(), mapSubscription.announcements(), false, UUID.randomUUID());
    }
}
