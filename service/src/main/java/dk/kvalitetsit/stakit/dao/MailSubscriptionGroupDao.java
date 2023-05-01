package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;

import java.util.List;
import java.util.UUID;

public interface MailSubscriptionGroupDao {
    void insert(MailSubscriptionGroupsEntity mailSubscriptionGroupsEntity);

    void deleteByEmail(String email);

    void deleteByUuid(UUID uuid);

    List<SubscriptionGroupEntity> getSubscriptions();

    SubscriptionGroupEntity getSubscriptionByUuid(UUID uuid);

}
