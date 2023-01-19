package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record GroupModel(UUID uuid, String name, int displayOrder, String description) {
    public static GroupModel createInstance(String name, int displayOrder, String description) {
        return new GroupModel(null, name, displayOrder, description);
    }
}
