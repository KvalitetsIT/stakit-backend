package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record GroupConfigurationEntity(Long id, UUID uuid, String groupName, int displayOrder) {
    public static GroupConfigurationEntity createInstance(UUID uuid, String groupName, int displayOrder) {
        return new GroupConfigurationEntity(null, uuid, groupName, displayOrder);
    }
}
