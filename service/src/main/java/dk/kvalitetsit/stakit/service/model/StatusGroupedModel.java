package dk.kvalitetsit.stakit.service.model;

import java.util.List;

public record StatusGroupedModel(String groupName, List<StatusElementModel> status, String description) {
}
