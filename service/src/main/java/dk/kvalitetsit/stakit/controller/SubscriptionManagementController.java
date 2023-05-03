package dk.kvalitetsit.stakit.controller;


import dk.kvalitetsit.stakit.service.SubscriptionManagementService;
import dk.kvalitetsit.stakit.service.model.SubscriptionModel;
import org.openapitools.api.SubscriptionManagementApi;

import org.openapitools.model.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
public class SubscriptionManagementController  implements SubscriptionManagementApi {

    private final SubscriptionManagementService service;

    public SubscriptionManagementController(SubscriptionManagementService service) {
        this.service = service;
    }

    private Subscription map(SubscriptionModel subscriptionModel){
        Subscription sub =  new Subscription(subscriptionModel.email(), subscriptionModel.announcements(), subscriptionModel.groups());
        sub.setUuid(subscriptionModel.uuid().toString());
        return sub;
    }

    @Override
    public ResponseEntity<List<Subscription>> v1SubscriptionsGet() {
        return ResponseEntity.ok(service.getSubscriptions().stream().map(this::map).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Subscription> v1SubscriptionsUuidGet(UUID uuid) {
        Optional<SubscriptionModel> response = service.getSubscription(uuid);
        return response.map(subscriptionModel -> ResponseEntity.ok(map(subscriptionModel))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
