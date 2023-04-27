package dk.kvalitetsit.stakit.dao.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SubscriptionGroupEntity(UUID subUuid, String email, boolean announcements,  UUID groupUuid) {
    public static SubscriptionGroupEntity createInstance(UUID uuid, String email, boolean announcements, UUID group) {
        return new SubscriptionGroupEntity(uuid, email, announcements, group);
    }

}
