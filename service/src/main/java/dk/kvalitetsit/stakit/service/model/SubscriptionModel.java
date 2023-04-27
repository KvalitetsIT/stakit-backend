package dk.kvalitetsit.stakit.service.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record SubscriptionModel(UUID uuid, String email, List<UUID> groups, boolean announcements) {


    public static SubscriptionModel createInstance(SubscriptionModel sub1, SubscriptionModel sub2) {
        return new SubscriptionModel(sub1.uuid(), sub1.email(), Stream.concat(sub1.groups.stream(), sub2.groups.stream()).distinct().toList(), sub1.announcements());
    }
}
