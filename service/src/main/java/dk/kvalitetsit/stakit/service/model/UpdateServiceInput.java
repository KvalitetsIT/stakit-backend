package dk.kvalitetsit.stakit.service.model;

import java.time.OffsetDateTime;

public record UpdateServiceInput(String service, Status status, OffsetDateTime statusDateTime, String message) {
}
