package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record GroupConfigurationEntity(Long id, UUID uuid, String name, int displayOrder, String description, boolean display, boolean expanded) {
    public static GroupConfigurationEntity createInstance(UUID uuid, String name, int displayOrder, String description, boolean display, boolean expanded) {
        return new GroupConfigurationEntity(null, uuid, name, displayOrder, description, display, expanded);
    }
}
