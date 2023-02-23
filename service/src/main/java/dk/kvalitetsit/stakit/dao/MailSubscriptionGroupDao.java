package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionGroupsEntity;

public interface MailSubscriptionGroupDao {
    void insert(MailSubscriptionGroupsEntity mailSubscriptionGroupsEntity);

    void deleteByEmail(String email);
}
