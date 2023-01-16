package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record ServiceConfigurationEntityWithGroupUuid(Long id, UUID uuid, String service, String name, boolean ignoreServiceName, UUID groupUuid, String description) {
}
