package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.openapitools.api.StaKitApi;
import org.openapitools.model.Group;
import org.openapitools.model.StatusGrouped;
import org.openapitools.model.StatusUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
public class StakitController implements StaKitApi {
    private static final Logger logger = LoggerFactory.getLogger(StakitController.class);
    private final StatusUpdateService statusUpdateService;
    private final StatusGroupService statusGroupService;

    public StakitController(StatusUpdateService statusUpdateService, StatusGroupService statusGroupService) {
        this.statusUpdateService = statusUpdateService;
        this.statusGroupService = statusGroupService;
    }

    @Override
    public ResponseEntity<StatusGrouped> v1StatusGroupedGet() {
        logger.debug("Reading status");

        var groupStatus = statusGroupService.getStatusGrouped();

        var mappedResult = groupStatus.stream()
                .map(this::mapGroup)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new StatusGrouped().statusGroup(mappedResult));
    }

    private Group mapGroup(dk.kvalitetsit.stakit.service.model.StatusGrouped statusGrouped) {
        var group = new Group();
        group.setGroupName(statusGrouped.groupName());
        group.setStatus(new ArrayList<>());

        statusGrouped.status().forEach(x -> {
            var s = new org.openapitools.model.Status();
            s.setServiceName(x.statusName());
            s.setStatus(org.openapitools.model.Status.StatusEnum.fromValue(x.status().toString()));
//            s.setStatusTime(); TODO Set later
//            s.setMessage();

            group.addStatusItem(s);
        });

        return group;
    }

    @Override
    public ResponseEntity<Void> v1StatusPost(StatusUpdate statusUpdate) {
        logger.debug("Updating or creating status");

        statusUpdateService.updateStatus(new UpdateServiceInput(statusUpdate.getService(), statusUpdate.getServiceName(), Status.valueOf(Status.class, statusUpdate.getStatus().toString()), statusUpdate.getStatusTime(), statusUpdate.getMessage()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
