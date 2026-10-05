package dk.kvalitetsit.stakit.service.model;

import java.time.OffsetDateTime;

public record StatusPeriodModel(Status status, OffsetDateTime from, OffsetDateTime to, String message) {
}
