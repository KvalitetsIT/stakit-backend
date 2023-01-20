package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.BadRequestException;
import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.controller.mapper.GroupMapper;
import dk.kvalitetsit.stakit.controller.mapper.ServiceManagementMapper;
import dk.kvalitetsit.stakit.service.GroupService;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import org.openapitools.api.GroupManagementApi;
import org.openapitools.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GroupManagementController implements GroupManagementApi {
    private static final Logger logger = LoggerFactory.getLogger(GroupManagementController.class);
    private final GroupService groupService;

    public GroupManagementController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public ResponseEntity<List<Group>> v1GroupsGet() {
        var groups = groupService.getGroups();

        return ResponseEntity.ok(GroupMapper.mapGetGroups(groups));
    }

    @Override
    public ResponseEntity<CreateResponse> v1GroupsPost(GroupInput groupUpdate) {
        var resource = groupService.createGroup(GroupMapper.mapCreateGroup(groupUpdate));

        return ResponseEntity.status(HttpStatus.CREATED).header("Location", resource.toString()).body(new CreateResponse().uuid(resource)); // TODO Jeg skal nok være en rigtig URL.
    }

    @Override
    public ResponseEntity<Void> v1GroupsUuidDelete(UUID uuid) {
        var deleted = groupService.deleteGroup(uuid);

        if(!deleted) {
            throw new ResourceNotFoundException("Group with uuid %s not found.".formatted(uuid));
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Group> v1GroupsUuidGet(UUID uuid) {
        var group = groupService.getGroup(uuid);

        return ResponseEntity.ok(group.map(GroupMapper::mapGroup).orElseThrow(() -> new ResourceNotFoundException("Group with uuid %s not found".formatted(uuid))));
    }

    @Override
    public ResponseEntity<Void> v1GroupsUuidPatch(UUID uuid, GroupPatch groupPatch) {
        logger.debug("Patching group");

        try {
            var patched = groupService.patchGroup(uuid, GroupMapper.mapPatchGroup(groupPatch));

            if(!patched) {
                throw new ResourceNotFoundException("Group with uuid %s not found".formatted(uuid));
            }

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (InvalidDataException e){
            logger.info("Invalid data. Returning error.", e);

            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> v1GroupsUuidPut(UUID uuid, GroupInput groupUpdate) {
        var updated = groupService.updateGroup(GroupMapper.mapUpdateGroup(uuid, groupUpdate));

        if(!updated) {
            throw new ResourceNotFoundException("Group with uuid %s not found".formatted(uuid));
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<List<Services>> v1GroupsUuidServicesGet(UUID uuid) {
        var services = groupService.getServicesInGroup(uuid);

        if (services.isEmpty()) {
            throw new ResourceNotFoundException("Group with uuid %s not found".formatted(uuid));
        }

        return ResponseEntity.ok(ServiceManagementMapper.mapServices(services.get()));
    }
}
