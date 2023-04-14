package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;

import java.util.UUID;

public interface MailSubscriptionGroupDao {
    void insert(MailSubscriptionGroupsEntity mailSubscriptionGroupsEntity);

    void deleteByEmail(String email);

    void deleteByUuid(UUID uuid);
}
