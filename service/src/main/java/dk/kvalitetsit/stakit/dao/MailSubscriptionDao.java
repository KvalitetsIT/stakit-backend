package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;

import java.util.List;

public interface MailSubscriptionDao {
    long insert(MailSubscriptionEntity mailSubscriptionEntity);

    List<MailSubscriptionEntity> findSubscriptionsByServiceConfigurationId(long serviceConfigurationId);
}
