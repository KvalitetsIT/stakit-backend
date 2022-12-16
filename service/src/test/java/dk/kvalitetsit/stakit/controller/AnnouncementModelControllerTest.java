package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.service.AnnouncementService;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.AnnouncementCreate;
import org.openapitools.model.AnnouncementUpdate;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class AnnouncementModelControllerTest {
    private AnnouncementService announcementService;
    private AnnouncementController announcementController;

    @Before
    public void setup() {
        announcementService = Mockito.mock(AnnouncementService.class);
        announcementController = new AnnouncementController(announcementService);
    }
    @Test
    public void testCreate() {
        var uuid = UUID.randomUUID();

        var input = new AnnouncementCreate();
        input.setFromDatetime(OffsetDateTime.now());
        input.setToDatetime(OffsetDateTime.now());
        input.setSubject("subject");
        input.setMessage("message");

        Mockito.when(announcementService.createAnnouncement(Mockito.any())).thenReturn(uuid);

        var result = announcementController.v1AnnouncementsPost(input);
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(uuid, result.getBody().getUuid());

        Mockito.verify(announcementService, times(1)).createAnnouncement(Mockito.argThat(x -> {
            assertEquals(input.getMessage(), x.message());
            assertEquals(input.getSubject(), x.subject());
            assertEquals(input.getFromDatetime(), x.fromDatetime());
            assertEquals(input.getToDatetime(), x.toDatetime());
            assertNull( x.uuid());

            return true;
        }));
    }

    @Test
    public void testUpdate() {
        var uuid = UUID.randomUUID();
        var input = new AnnouncementUpdate();
        input.setFromDatetime(OffsetDateTime.now());
        input.setToDatetime(OffsetDateTime.now());
        input.setSubject("subject");
        input.setMessage("message");

        Mockito.when(announcementService.updateAnnouncement(Mockito.any())).thenReturn(true);

        var result = announcementController.v1AnnouncementsUuidPut(uuid, input);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        Mockito.verify(announcementService, times(1)).updateAnnouncement(Mockito.argThat(x -> {
            assertEquals(input.getMessage(), x.message());
            assertEquals(input.getSubject(), x.subject());
            assertEquals(input.getFromDatetime(), x.fromDatetime());
            assertEquals(input.getToDatetime(), x.toDatetime());
            assertEquals(uuid, x.uuid());

            return true;
        }));
    }

    @Test
    public void testUpdateNotFound() {
        var uuid = UUID.randomUUID();
        var input = new AnnouncementUpdate();
        input.setFromDatetime(OffsetDateTime.now());
        input.setToDatetime(OffsetDateTime.now());
        input.setSubject("subject");
        input.setMessage("message");

        Mockito.when(announcementService.updateAnnouncement(Mockito.any())).thenReturn(false);


        var expectedException = assertThrows(ResourceNotFoundException.class, () ->announcementController.v1AnnouncementsUuidPut(uuid, input));
        assertNotNull(expectedException);
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());

        Mockito.verify(announcementService, times(1)).updateAnnouncement(Mockito.argThat(x -> {
            assertEquals(input.getMessage(), x.message());
            assertEquals(input.getSubject(), x.subject());
            assertEquals(input.getFromDatetime(), x.fromDatetime());
            assertEquals(input.getToDatetime(), x.toDatetime());
            assertEquals(uuid, x.uuid());

            return true;
        }));
    }

    @Test
    public void testDelete() {
        var uuid = UUID.randomUUID();

        Mockito.when(announcementService.deleteAnnouncement(Mockito.any())).thenReturn(true);

        var result = announcementController.v1AnnouncementsUuidDelete(uuid);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        Mockito.verify(announcementService, times(1)).deleteAnnouncement(uuid);
    }

    @Test
    public void testDeleteNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(announcementService.deleteAnnouncement(Mockito.any())).thenReturn(false);

        var expectedException = assertThrows(ResourceNotFoundException.class, () ->announcementController.v1AnnouncementsUuidDelete(uuid));
        assertNotNull(expectedException);
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());

        Mockito.verify(announcementService, times(1)).deleteAnnouncement(uuid);

    }

    @Test
    public void testGet() {
        var uuid = UUID.randomUUID();

        var announcement = new AnnouncementModel(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "message");

        Mockito.when(announcementService.getAnnouncement(Mockito.any())).thenReturn(Optional.of(announcement));

        var result = announcementController.v1AnnouncementsUuidGet(uuid);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        var body = result.getBody();
        assertEquals(announcement.message(), body.getMessage());
        assertEquals(announcement.subject(), body.getSubject());
        assertEquals(announcement.fromDatetime(), body.getFromDatetime());
        assertEquals(announcement.toDatetime(), body.getToDatetime());
        assertEquals(announcement.uuid(), body.getUuid());

        Mockito.verify(announcementService, times(1)).getAnnouncement(uuid);
    }

    @Test
    public void testGetNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(announcementService.getAnnouncement(Mockito.any())).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> announcementController.v1AnnouncementsUuidGet(uuid));
        assertNotNull(expectedException);
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());

        Mockito.verify(announcementService, times(1)).getAnnouncement(uuid);
    }
}
