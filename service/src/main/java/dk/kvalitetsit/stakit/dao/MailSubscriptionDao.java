package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.MailSubscriptionEntity;
import dk.kvalitetsit.stakit.dao.entity.SubscriptionGroupEntity;

import java.util.List;
import java.util.UUID;

public interface MailSubscriptionDao {
    long insert(MailSubscriptionEntity mailSubscriptionEntity);

    List<MailSubscriptionEntity> findSubscriptionsByServiceConfigurationId(long serviceConfigurationId);

    boolean updateConfirmedByConfirmationUuid(UUID confirmationUuid);

    void deleteByEmail(String email);

    int deleteByUuid(UUID uuid);

    List<MailSubscriptionEntity> findAnnouncementSubscriptions();

}