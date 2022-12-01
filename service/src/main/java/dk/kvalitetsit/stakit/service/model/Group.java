package dk.kvalitetsit.stakit.service.model;

import java.util.UUID;

public record Group(UUID uuid, String name, int displayOrder) {
    public static Group createInstance(String name, int displayOrder) {
        return new Group(null, name, displayOrder);
    }
}
