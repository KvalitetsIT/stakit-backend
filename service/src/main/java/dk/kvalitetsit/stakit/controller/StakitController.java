package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.mapper.GroupMapper;
import dk.kvalitetsit.stakit.service.GroupService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
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
    private final StatusUpdateService statusUpdateService;
    private final StatusGroupService statusGroupService;
    private final GroupService groupService;

    public StakitController(StatusUpdateService statusUpdateService, StatusGroupService statusGroupService, GroupService groupService) {
        this.statusUpdateService = statusUpdateService;
        this.statusGroupService = statusGroupService;
        this.groupService = groupService;
    }

    @Override
    public ResponseEntity<List<Group>> v1GroupsGet() {
        var groups = groupService.getGroups();

        return ResponseEntity.ok(GroupMapper.mapGetGroups(groups));
    }

    @Override
    public ResponseEntity<Void> v1GroupsPost(GroupInput groupUpdate) {
        var resource = groupService.createGroup(GroupMapper.mapCreateGroup(groupUpdate));

        return ResponseEntity.status(HttpStatus.CREATED).header("Location", resource.toString()).build(); // TODO Jeg skal nok være en rigtig URL.
    }

    @Override
    public ResponseEntity<Void> v1GroupsUuidPut(UUID uuid, GroupInput groupUpdate) {
        var updated = groupService.updateGroup(GroupMapper.mapUpdateGroup(uuid, groupUpdate));

        if(updated) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<StatusGrouped> v1ServiceStatusGroupedGet() {
        logger.debug("Reading status");

        var groupStatus = statusGroupService.getStatusGrouped();

        var mappedResult = groupStatus.stream()
                .map(this::mapGroup)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new StatusGrouped().statusGroup(mappedResult));
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

    @Override
    public ResponseEntity<Void> v1StatusPost(StatusUpdate statusUpdate) {
        logger.debug("Updating or creating status");

        statusUpdateService.updateStatus(new UpdateServiceInput(statusUpdate.getService(), statusUpdate.getServiceName(), Status.valueOf(Status.class, statusUpdate.getStatus().toString()), statusUpdate.getStatusTime(), statusUpdate.getMessage()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
