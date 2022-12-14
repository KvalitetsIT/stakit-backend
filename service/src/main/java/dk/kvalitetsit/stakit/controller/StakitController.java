package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.BadRequestException;
import dk.kvalitetsit.stakit.controller.mapper.AnnouncementMapper;
import dk.kvalitetsit.stakit.service.AnnouncementService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.SubscriptionService;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.Subscription;
import dk.kvalitetsit.stakit.session.PublicApi;
import org.openapitools.api.StaKitApi;
import org.openapitools.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost") // TODO Jeg skal nok fjernes igen.
public class StakitController implements StaKitApi {
    private static final Logger logger = LoggerFactory.getLogger(StakitController.class);
    private final StatusGroupService statusGroupService;
    private final AnnouncementService announcementService;
    private final SubscriptionService subscriptionService;

    public StakitController(StatusGroupService statusGroupService,
                            AnnouncementService announcementService,
                            SubscriptionService subscriptionService) {
        this.statusGroupService = statusGroupService;
        this.announcementService = announcementService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    @PublicApi
    public ResponseEntity<List<AnnouncementsToShow>> v1AnnouncementsToShowGet() {
        logger.debug("Reading announcements.");

        var announcements = announcementService.getAnnouncements();

        return ResponseEntity.ok(AnnouncementMapper.mapAnnouncementsToShow(announcements));
    }

    @Override
    @PublicApi
    public ResponseEntity<StatusGrouped> v1ServiceStatusGroupedGet() {
        logger.debug("Reading status");

        var groupStatus = statusGroupService.getStatusGrouped();

        var mappedResult = groupStatus.stream()
                .map(this::mapGroup)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new StatusGrouped().statusGroup(mappedResult));
    }

    @Override
    @PublicApi
    public ResponseEntity<Void> v1SubscribeConfirmUuidGet(UUID uuid) {
        subscriptionService.confirmSubscription(uuid);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PublicApi
    public ResponseEntity<CreateResponse> v1SubscribePost(Subscribe subscribe) {
        logger.debug("Subscribing to updates.");

        try {
            var result = subscriptionService.subscribe(mapSubscription(subscribe));

            return ResponseEntity.status(HttpStatus.CREATED).body(new CreateResponse().uuid(result));
        } catch(InvalidDataException e) {
            logger.info("Invalid data. Returning error.", e);

            throw new BadRequestException(e.getMessage());
        }
    }

    private Subscription mapSubscription(Subscribe subscribe) {
        return new Subscription(subscribe.getEmail(), subscribe.getGroups(), subscribe.getAnnouncements());
    }

    private Grouped mapGroup(dk.kvalitetsit.stakit.service.model.StatusGrouped statusGrouped) {
        var group = new Grouped();
        group.setGroupName(statusGrouped.groupName());
        group.setServices(new ArrayList<>());

        statusGrouped.status().forEach(x -> {
            var s = new org.openapitools.model.ServiceStatus();
            s.setServiceName(x.statusName());
            s.setStatus(org.openapitools.model.ServiceStatus.StatusEnum.fromValue(x.status().toString()));

            group.addServicesItem(s);
        });

        return group;
    }
}
