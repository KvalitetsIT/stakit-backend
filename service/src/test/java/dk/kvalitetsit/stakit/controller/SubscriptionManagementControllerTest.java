package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.SubscriptionManagementService;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionManagementControllerTest {
    private SubscriptionManagementController subscriptionController;
    private SubscriptionManagementService subscriptionManagementService;

    @Before
    public void setup() {
        subscriptionManagementService = Mockito.mock(SubscriptionManagementService.class);
        subscriptionController = new SubscriptionManagementController(subscriptionManagementService);
    }

    @Test
    public void testGetSubscriptionMultipleGroups() {
        var email = "email";
        var input = UUID.randomUUID();

        var groupOne = UUID.randomUUID();
        var groupTwo = UUID.randomUUID();

        var subscription = new SubscriptionModel(input, email, List.of(groupOne, groupTwo), true);

        Mockito.when(subscriptionManagementService.getSubscription(input)).thenReturn(Optional.of(subscription));

        var result = subscriptionController.v1SubscriptionsUuidGet(input);

        assertNotNull(result);

        var body = result.getBody();
        assertNotNull(body);

        assertEquals(true, body.getAnnouncements());
        assertEquals(email, body.getEmail());
        assertEquals(input.toString(), body.getUuid());
        assertEquals(2, body.getGroups().size());
        assertEquals(subscription.groups(), body.getGroups());
    }

    @Test
    public void testGetSubscriptionNoGroups() {
        var email = "email";
        var input = UUID.randomUUID();

        var subscription = new SubscriptionModel(input, email, Collections.emptyList(), true);

        Mockito.when(subscriptionManagementService.getSubscription(input)).thenReturn(Optional.of(subscription));

        var result = subscriptionController.v1SubscriptionsUuidGet(input);

        assertNotNull(result);

        var body = result.getBody();
        assertNotNull(body);

        assertEquals(true, body.getAnnouncements());
        assertEquals(email, body.getEmail());
        assertEquals(input.toString(), body.getUuid());
        assertEquals(0, body.getGroups().size());
    }
}
