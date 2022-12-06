package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundExceptionAbstract;
import dk.kvalitetsit.stakit.controller.mapper.GroupMapper;
import dk.kvalitetsit.stakit.service.GroupService;
import org.openapitools.api.GroupManagementApi;
import org.openapitools.model.CreateResponse;
import org.openapitools.model.Group;
import org.openapitools.model.GroupInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost") // TODO Jeg skal nok fjernes igen.
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
    public ResponseEntity<Void> v1GroupsUuidPut(UUID uuid, GroupInput groupUpdate) {
        var updated = groupService.updateGroup(GroupMapper.mapUpdateGroup(uuid, groupUpdate));

        if(!updated) {
            throw new ResourceNotFoundExceptionAbstract("Group with uuid %s not found".formatted(uuid));
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
