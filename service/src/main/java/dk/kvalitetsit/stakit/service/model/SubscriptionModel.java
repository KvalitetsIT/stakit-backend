package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record SubscriptionModel(UUID uuid, String email, List<UUID> groups, boolean announcements) {


}
