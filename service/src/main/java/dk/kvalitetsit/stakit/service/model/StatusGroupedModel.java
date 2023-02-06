package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;

public record StatusGroupedModel(String groupName, List<StatusElementModel> status, String description, UUID groupUuid) {
}
