package dk.kvalitetsit.stakit.dao.entity;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record AnnouncementEntity(Long id, UUID uuid, OffsetDateTime fromDatetime, OffsetDateTime toDatetime, String subject, String message) {
    public static AnnouncementEntity createInstance(UUID uuid, OffsetDateTime fromDate, OffsetDateTime toDate, String subject, String message) {
        return new AnnouncementEntity(null, uuid, fromDate.truncatedTo(ChronoUnit.SECONDS), toDate != null ? toDate.truncatedTo(ChronoUnit.SECONDS) : null, subject, message);
    }
}
