package dk.kvalitetsit.stakit.dao.entity;

import java.time.OffsetDateTime;

public record StatusEntity(Long id, String service, String status, OffsetDateTime statusTime, String message) {
    public static StatusEntity createInstance(String service, String status, OffsetDateTime statusTime, String message) {
        return new StatusEntity(null, service, status, statusTime, message);
    }
}
