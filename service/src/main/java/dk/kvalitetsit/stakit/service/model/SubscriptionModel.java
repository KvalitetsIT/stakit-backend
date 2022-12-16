package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;

public record SubscriptionModel(String email, List<UUID> groups, boolean announcements) {
}
