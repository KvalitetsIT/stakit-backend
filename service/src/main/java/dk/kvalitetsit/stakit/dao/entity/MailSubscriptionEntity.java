package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record MailSubscriptionEntity(Long id, UUID uuid, String email, boolean announcements, boolean confirmed, UUID confirmIdentifier) {
    public static MailSubscriptionEntity createInstance(UUID uuid, String email, boolean announcements, boolean confirmed, UUID confirmIdentifier) {
        return new MailSubscriptionEntity(null, uuid, email, announcements, confirmed, confirmIdentifier);
    }
}