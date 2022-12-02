package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record ServiceConfigurationEntity(Long id, UUID uuid, String service, String name, boolean ignoreServiceName, Long groupConfigurationId) {
    public static ServiceConfigurationEntity createInstance(String service, UUID uuid, String name, boolean ignoreServiceName, Long groupConfigurationId) {
        return new ServiceConfigurationEntity(null, uuid, service, name, ignoreServiceName, groupConfigurationId);
    }
}
