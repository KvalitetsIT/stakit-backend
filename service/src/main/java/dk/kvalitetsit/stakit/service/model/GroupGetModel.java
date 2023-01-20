package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;

public record GroupGetModel(UUID uuid, String name, int displayOrder, List<UUID> services, String description) {
}