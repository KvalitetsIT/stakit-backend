package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record GroupModel(UUID uuid, String name, int displayOrder) {
    public static GroupModel createInstance(String name, int displayOrder) {
        return new GroupModel(null, name, displayOrder);
    }
}
