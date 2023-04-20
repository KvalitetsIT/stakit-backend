package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;

public record GroupModel(UUID uuid, String name, int displayOrder, String description, List<UUID> services, boolean display) {
    public static GroupModel createInstance(String name, int displayOrder, String description, List<UUID> services, boolean display) {
        return new GroupModel(null, name, displayOrder, description, services, display);
    }
}
