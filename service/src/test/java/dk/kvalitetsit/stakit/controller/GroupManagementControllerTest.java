package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.service.GroupService;
import dk.kvalitetsit.stakit.service.model.Group;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.GroupInput;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertEquals(expectedUuid.toString(), result.getHeaders().get("location").stream().findFirst().get());
        assertEquals(expectedUuid, result.getBody().getUuid());
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

        var expectedException = assertThrows(ResourceNotFoundException.class, () ->groupManagementController.v1GroupsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals("Group with uuid %s not found".formatted(uuid), expectedException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());

        Mockito.verify(groupService, times(1)).updateGroup(serviceInput);
    }

    @Test
    public void testDelete() {
        var uuid = UUID.randomUUID();

        Mockito.when(groupService.deleteGroup(uuid)).thenReturn(true);

        var result = groupManagementController.v1GroupsUuidDelete(uuid);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        Mockito.verify(groupService, times(1)).deleteGroup(uuid);
    }

    @Test
    public void testDeleteNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(groupService.deleteGroup(uuid)).thenReturn(false);

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> groupManagementController.v1GroupsUuidDelete(uuid));
        assertNotNull(expectedException);

        Mockito.verify(groupService, times(1)).deleteGroup(uuid);
    }
}
