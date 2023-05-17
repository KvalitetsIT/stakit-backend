package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;

import javax.mail.MessagingException;
import java.util.UUID;

public interface SubscriptionService {
    UUID subscribe(SubscriptionModel mapSubscriptionModel) throws InvalidDataException, MessagingException;

    void confirmSubscription(UUID confirmationUuid);

    boolean delete(UUID uuid);
}
