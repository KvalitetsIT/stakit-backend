package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;

public record Subscription(String email, List<UUID> groups, boolean announcements) {
}
