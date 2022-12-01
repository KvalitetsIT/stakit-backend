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

public class StakitControllerTest {
    private StakitController stakitController;
    private StatusUpdateService statusUpdateService;
    private StatusGroupService statusGroupService;
    private GroupService groupService;

    @Before
    public void setup() {
        statusUpdateService = Mockito.mock(StatusUpdateService.class);
        statusGroupService = Mockito.mock(StatusGroupService.class);
        groupService = Mockito.mock(GroupService.class);

        stakitController = new StakitController(statusUpdateService, statusGroupService, groupService);
    }

    @Test
    public void testStatusUpdate() {
        var input = new StatusUpdate();
        input.setService("service_id");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        var result = stakitController.v1StatusPost(input);

        assertNotNull(result);

        Mockito.verify(statusUpdateService, times(1)).updateStatus(Mockito.argThat(x -> {
            assertEquals(input.getMessage(), x.message());
            assertEquals(input.getService(), x.service());
            assertEquals(input.getStatusTime(), x.statusDateTime());
            assertEquals(Status.OK, x.status());

            return true;
        }));
    }

    @Test
    public void testStatusGetGrouped() {
        var groupOne = new StatusGrouped("Default", new ArrayList<>());
        groupOne.status().add(new StatusElement(Status.OK, "In Default Group"));

        var groupTwo = new StatusGrouped("Group Two", new ArrayList<>());
        groupTwo.status().add(new StatusElement(Status.OK, "Name"));
        groupTwo.status().add(new StatusElement(Status.NOT_OK, "Name Two"));

        var serviceResponse = new ArrayList<StatusGrouped>();
        serviceResponse.add(groupOne);
        serviceResponse.add(groupTwo);

        Mockito.when(statusGroupService.getStatusGrouped()).thenReturn(serviceResponse);

        var result = stakitController.v1ServiceStatusGroupedGet();

        assertNotNull(result);
        assertNotNull(result.getBody());

        var statusGroupList = result.getBody().getStatusGroup();
        assertEquals(2, statusGroupList.size());

        // Assert first one
        var statusGroup = statusGroupList.get(0);
        assertEquals(groupOne.groupName(), statusGroup.getGroupName());

        assertEquals(groupOne.status().get(0).statusName(), statusGroup.getServices().get(0).getServiceName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.OK, statusGroup.getServices().get(0).getStatus());

        // Assert second one
        statusGroup = statusGroupList.get(1);
        assertEquals(groupTwo.groupName(), statusGroup.getGroupName());

        assertEquals(groupTwo.status().get(0).statusName(), statusGroup.getServices().get(0).getServiceName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.OK, statusGroup.getServices().get(0).getStatus());

        assertEquals(groupTwo.status().get(1).statusName(), statusGroup.getServices().get(1).getServiceName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.NOT_OK, statusGroup.getServices().get(1).getStatus());
    }

    @Test
    public void testGetGroups() {
        var groupOne = new Group(UUID.randomUUID(), "Name 1", 20);
        var groupTwo = new Group(UUID.randomUUID(), "Name 2", 30);

        Mockito.when(groupService.getGroups()).thenReturn(Arrays.asList(groupOne, groupTwo));

        var result = stakitController.v1GroupsGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        var body = result.getBody();
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

        var result = stakitController.v1GroupsPost(input);
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

        var result = stakitController.v1GroupsUuidPut(uuid, input);
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

        var result = stakitController.v1GroupsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(404, result.getStatusCodeValue());

        Mockito.verify(groupService, times(1)).updateGroup(serviceInput);
    }
}
