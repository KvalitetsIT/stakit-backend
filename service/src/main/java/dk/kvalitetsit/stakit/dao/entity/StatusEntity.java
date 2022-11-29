package dk.kvalitetsit.stakit.dao.entity;

import java.time.OffsetDateTime;

public record StatusEntity(Long id, Long statusConfigurationId, String status, OffsetDateTime statusTime, String message) {
    public static StatusEntity createInstance(Long statusConfigurationId, String status, OffsetDateTime statusTime, String message) {
        return new StatusEntity(null, statusConfigurationId, status, statusTime, message);
    }
}
