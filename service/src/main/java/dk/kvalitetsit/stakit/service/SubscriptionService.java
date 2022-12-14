package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.Subscription;

import java.util.UUID;

public interface SubscriptionService {
    UUID subscribe(Subscription mapSubscription) throws InvalidDataException;

    void confirmSubscription(UUID confirmationUuid);
}
