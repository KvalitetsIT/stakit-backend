package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import java.util.UUID;

public interface SubscriptionService {
    UUID subscribe(SubscriptionModel mapSubscriptionModel) throws InvalidDataException;

    void confirmSubscription(UUID confirmationUuid);

    boolean delete(UUID uuid);
}
