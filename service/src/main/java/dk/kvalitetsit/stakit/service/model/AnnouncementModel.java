package dk.kvalitetsit.stakit.service.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AnnouncementModel(UUID uuid, OffsetDateTime fromDatetime, OffsetDateTime toDatetime, String subject, String message) {
}
