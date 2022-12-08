package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.AnnouncementService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.model.Announcement;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElement;
import dk.kvalitetsit.stakit.service.model.StatusGrouped;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StakitControllerTest {
    private StakitController stakitController;
    private StatusGroupService statusGroupService;
    private AnnouncementService announcementService;

    @Before
    public void setup() {
        statusGroupService = Mockito.mock(StatusGroupService.class);
        announcementService = Mockito.mock(AnnouncementService.class);

        stakitController = new StakitController(statusGroupService, announcementService);
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
    public void testGetAnnouncements() {
        var announcementOne = new Announcement(UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject one", "message one");
        var announcementTwo = new Announcement(UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject two", "message two");

        Mockito.when(announcementService.getAnnouncements()).thenReturn(Arrays.asList(announcementOne, announcementTwo));

        var result = stakitController.v1AnnouncementsToShowGet();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        var body = result.getBody();
        assertEquals(2, body.size());

        assertEquals(announcementOne.message(), body.get(0).getMessage());
        assertEquals(announcementOne.subject(), body.get(0).getSubject());
        assertEquals(announcementOne.toDatetime(), body.get(0).getToDatetime());
        assertEquals(announcementOne.fromDatetime(), body.get(0).getFromDatetime());

        assertEquals(announcementTwo.message(), body.get(1).getMessage());
        assertEquals(announcementTwo.subject(), body.get(1).getSubject());
        assertEquals(announcementTwo.toDatetime(), body.get(1).getToDatetime());
        assertEquals(announcementTwo.fromDatetime(), body.get(1).getFromDatetime());
    }
}
