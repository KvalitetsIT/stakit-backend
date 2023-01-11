package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.BadRequestException;
import dk.kvalitetsit.stakit.service.AnnouncementService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.SubscriptionService;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElementModel;
import dk.kvalitetsit.stakit.service.model.StatusGroupedModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.Subscribe;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

public class StakitControllerTest {
    private StakitController stakitController;
    private StatusGroupService statusGroupService;
    private AnnouncementService announcementService;
    private SubscriptionService subscriptionService;

    @Before
    public void setup() {
        statusGroupService = Mockito.mock(StatusGroupService.class);
        announcementService = Mockito.mock(AnnouncementService.class);
        subscriptionService = Mockito.mock(SubscriptionService.class);

        stakitController = new StakitController(statusGroupService, announcementService, subscriptionService);
    }

    @Test
    public void testStatusGetGrouped() {
        var groupOne = new StatusGroupedModel("Default", new ArrayList<>());
        groupOne.status().add(new StatusElementModel(Status.OK, "In Default Group"));

        var groupTwo = new StatusGroupedModel("Group Two", new ArrayList<>());
        groupTwo.status().add(new StatusElementModel(Status.OK, "Name"));
        groupTwo.status().add(new StatusElementModel(Status.NOT_OK, "Name Two"));

        var serviceResponse = new ArrayList<StatusGroupedModel>();
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
        assertEquals(groupOne.groupName(), statusGroup.getName());

        assertEquals(groupOne.status().get(0).statusName(), statusGroup.getServices().get(0).getName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.OK, statusGroup.getServices().get(0).getStatus());

        // Assert second one
        statusGroup = statusGroupList.get(1);
        assertEquals(groupTwo.groupName(), statusGroup.getName());

        assertEquals(groupTwo.status().get(0).statusName(), statusGroup.getServices().get(0).getName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.OK, statusGroup.getServices().get(0).getStatus());

        assertEquals(groupTwo.status().get(1).statusName(), statusGroup.getServices().get(1).getName());
        assertEquals(org.openapitools.model.ServiceStatus.StatusEnum.NOT_OK, statusGroup.getServices().get(1).getStatus());
    }

    @Test
    public void testGetAnnouncements() {
        var announcementOne = new AnnouncementModel(UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject one", "message one");
        var announcementTwo = new AnnouncementModel(UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject two", "message two");

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

    @Test
    public void testSubscribe() {
        var input = new Subscribe();
        input.setEmail("email");
        input.setAnnouncements(true);
        input.setGroups(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        var subscriptionUuid = UUID.randomUUID();

        Mockito.when(subscriptionService.subscribe(Mockito.any())).thenReturn(subscriptionUuid);

        var result = stakitController.v1SubscribePost(input);
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(subscriptionUuid, result.getBody().getUuid());

        Mockito.verify(subscriptionService, times(1)).subscribe(Mockito.argThat(x -> {
            assertEquals(input.getEmail(), x.email());
            assertEquals(input.getAnnouncements(), x.announcements());
            assertEquals(input.getGroups(), x.groups());

            return true;
        }));
    }

    @Test
    public void testSubscribeGroupNotFound() {
        var input = new Subscribe();
        input.setEmail("email");
        input.setAnnouncements(true);
        input.setGroups(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        Mockito.when(subscriptionService.subscribe(Mockito.any())).thenThrow(new InvalidDataException("message"));

        var result = assertThrows(BadRequestException.class, () -> stakitController.v1SubscribePost(input));
        assertNotNull(result);
        assertEquals("message", result.getMessage());

        Mockito.verify(subscriptionService, times(1)).subscribe(Mockito.argThat(x -> {
            assertEquals(input.getEmail(), x.email());
            assertEquals(input.getAnnouncements(), x.announcements());
            assertEquals(input.getGroups(), x.groups());

            return true;
        }));
    }

    @Test
    public void testSubscribeConfirm() {
        var confirmationUuid = UUID.randomUUID();

        stakitController.v1SubscribeConfirmUuidGet(confirmationUuid);

        Mockito.verify(subscriptionService, times(1)).confirmSubscription(confirmationUuid);
    }
}
