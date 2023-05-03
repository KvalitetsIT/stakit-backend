package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionManagementService {
    List<SubscriptionModel> getSubscriptions();

    Optional<SubscriptionModel> getSubscription(UUID uuid);
}
