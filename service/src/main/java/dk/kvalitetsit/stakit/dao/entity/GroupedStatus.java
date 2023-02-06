package dk.kvalitetsit.stakit.dao.entity;

import java.util.UUID;

public record GroupedStatus(String groupName, String status, String serviceName, String groupDescription, String serviceDescription, UUID groupUuid) {
}
