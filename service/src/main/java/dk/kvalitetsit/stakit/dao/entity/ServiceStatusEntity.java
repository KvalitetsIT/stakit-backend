package dk.kvalitetsit.stakit.dao.entity;

import java.time.OffsetDateTime;

public record ServiceStatusEntity(Long id, Long serviceConfigurationId, String status, OffsetDateTime statusTime, String message) {
    public static ServiceStatusEntity createInstance(Long serviceConfigurationId, String status, OffsetDateTime statusTime, String message) {
        return new ServiceStatusEntity(null, serviceConfigurationId, status, statusTime, message);
    }
}
