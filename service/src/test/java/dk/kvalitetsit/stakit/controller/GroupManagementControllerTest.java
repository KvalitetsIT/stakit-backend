package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.GroupService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Group;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElement;
import dk.kvalitetsit.stakit.service.model.StatusGrouped;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.GroupInput;
import org.openapitools.model.StatusUpdate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

public class GroupManagementControllerTest {
    private GroupManagementController groupManagementController;
    private GroupService groupService;

    @Before
    public void setup() {
        groupService = Mockito.mock(GroupService.class);

        groupManagementController = new GroupManagementController(groupService);
    }

    @Test
    public void testGetGroups() {
        var groupOne = new Group(UUID.randomUUID(), "Name 1", 20);
        var groupTwo = new Group(UUID.randomUUID(), "Name 2", 30);

        Mockito.when(groupService.getGroups()).thenReturn(Arrays.asList(groupOne, groupTwo));

        var result = groupManagementController.v1GroupsGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        var body = result.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        assertEquals(groupOne.uuid(), body.get(0).getId());
        assertEquals(groupOne.name(), body.get(0).getName());
        assertEquals(groupOne.displayOrder(), body.get(0).getDisplayOrder());

        assertEquals(groupTwo.uuid(), body.get(1).getId());
        assertEquals(groupTwo.name(), body.get(1).getName());
        assertEquals(groupTwo.displayOrder(), body.get(1).getDisplayOrder());
    }

    @Test
    public void testInsertGroup() {
        var input = new GroupInput();
        input.setName("name");
        input.setDisplayOrder(10);

        var expectedUuid = UUID.randomUUID();

        Mockito.when(groupService.createGroup(Group.createInstance(input.getName(), input.getDisplayOrder()))).thenReturn(expectedUuid);

        var result = groupManagementController.v1GroupsPost(input);
        assertNotNull(result);
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void testUpdateGroup() {
        var uuid = UUID.randomUUID();
        var input = new GroupInput();
        input.setName("name");
        input.setDisplayOrder(10);

        var serviceInput = new Group(uuid, input.getName(), input.getDisplayOrder());

        Mockito.when(groupService.updateGroup(serviceInput)).thenReturn(true);

        var result = groupManagementController.v1GroupsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(groupService, times(1)).updateGroup(serviceInput);
    }

    @Test
    public void testUpdateGroupNotFound() {
        var uuid = UUID.randomUUID();
        var input = new GroupInput();
        input.setName("name");
        input.setDisplayOrder(10);

        var serviceInput = new Group(uuid, input.getName(), input.getDisplayOrder());

        Mockito.when(groupService.updateGroup(serviceInput)).thenReturn(false);

        var result = groupManagementController.v1GroupsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(404, result.getStatusCodeValue());

        Mockito.verify(groupService, times(1)).updateGroup(serviceInput);
    }
}
