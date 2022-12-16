package dk.kvalitetsit.stakit.service.model;

import java.time.OffsetDateTime;

public record UpdateServiceModel(String service, String serviceName, Status status, OffsetDateTime statusDateTime, String message) {
}
