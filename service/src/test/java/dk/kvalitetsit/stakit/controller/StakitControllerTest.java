package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElement;
import dk.kvalitetsit.stakit.service.model.StatusGrouped;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StakitControllerTest {
    private StakitController stakitController;
    private StatusGroupService statusGroupService;

    @Before
    public void setup() {
        statusGroupService = Mockito.mock(StatusGroupService.class);

        stakitController = new StakitController(statusGroupService);
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
}
