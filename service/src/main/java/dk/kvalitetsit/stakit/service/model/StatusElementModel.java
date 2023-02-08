package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record StatusElementModel(Status status, String statusName, String description, UUID uuid) {
}
