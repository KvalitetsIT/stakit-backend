package dk.kvalitetsit.stakit.service.model;

import java.util.List;

public record StatusGrouped(String groupName, List<StatusElement> status) {
}
