package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.AnnouncementDao;
import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import dk.kvalitetsit.stakit.service.model.AnnouncementModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class AnnouncementModelServiceImplTest {
    private AnnouncementDao announcementDao;
    private AnnouncementServiceImpl announcementService;

    @Before
    public void setup() {
        announcementDao = Mockito.mock(AnnouncementDao.class);
        announcementService = new AnnouncementServiceImpl(announcementDao);
    }

    @Test
    public void testCreate() {
        var input = new AnnouncementModel(null, OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "message");

        Mockito.when(announcementDao.insert(Mockito.any())).thenReturn(10L);

        var result = announcementService.createAnnouncement(input);
        assertNotNull(result);

        Mockito.verify(announcementDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(input.fromDatetime().truncatedTo(ChronoUnit.SECONDS), x.fromDatetime());
            assertEquals(input.toDatetime().truncatedTo(ChronoUnit.SECONDS), x.toDatetime());
            assertEquals(input.subject(), x.subject());
            assertEquals(input.message(), x.message());
            assertEquals(result, x.uuid());

            return true;
        }));
    }

    @Test
    public void testUpdate() {
        var input = new AnnouncementModel(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "message");

        Mockito.when(announcementDao.updateByUuid(Mockito.any())).thenReturn(true);

        var result = announcementService.updateAnnouncement(input);
        assertTrue(result);

        Mockito.verify(announcementDao, times(1)).updateByUuid(Mockito.argThat(x -> {
            assertEquals(input.fromDatetime().truncatedTo(ChronoUnit.SECONDS), x.fromDatetime());
            assertEquals(input.toDatetime().truncatedTo(ChronoUnit.SECONDS), x.toDatetime());
            assertEquals(input.subject(), x.subject());
            assertEquals(input.message(), x.message());
            assertEquals(input.uuid(), x.uuid());

            return true;
        }));
    }

    @Test
    public void testUpdateNotFound() {
        var input = new AnnouncementModel(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "message");

        Mockito.when(announcementDao.updateByUuid(Mockito.any())).thenReturn(false);

        var result = announcementService.updateAnnouncement(input);
        assertFalse(result);

        Mockito.verify(announcementDao, times(1)).updateByUuid(Mockito.argThat(x -> {
            assertEquals(input.fromDatetime().truncatedTo(ChronoUnit.SECONDS), x.fromDatetime());
            assertEquals(input.toDatetime().truncatedTo(ChronoUnit.SECONDS), x.toDatetime());
            assertEquals(input.subject(), x.subject());
            assertEquals(input.message(), x.message());
            assertEquals(input.uuid(), x.uuid());

            return true;
        }));
    }

    @Test
    public void testDelete() {
        var input = UUID.randomUUID();

        Mockito.when(announcementDao.deleteByUuid(Mockito.any())).thenReturn(true);

        var result = announcementService.deleteAnnouncement(input);
        assertTrue(result);

        Mockito.verify(announcementDao, times(1)).deleteByUuid(Mockito.eq(input));
    }

    @Test
    public void testDeleteNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(announcementDao.deleteByUuid(Mockito.any())).thenReturn(false);

        var result = announcementService.deleteAnnouncement(input);
        assertFalse(result);

        Mockito.verify(announcementDao, times(1)).deleteByUuid(Mockito.eq(input));
    }

    @Test
    public void testGet() {
        var input = UUID.randomUUID();

        var announcementEntity = new AnnouncementEntity(10L, UUID.randomUUID(), OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS), OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS), "subject", "message");

        Mockito.when(announcementDao.getByUuid(Mockito.any())).thenReturn(Optional.of(announcementEntity));

        var result = announcementService.getAnnouncement(input);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(announcementEntity.uuid(), result.get().uuid());
        assertEquals(announcementEntity.fromDatetime(), result.get().fromDatetime());
        assertEquals(announcementEntity.toDatetime(), result.get().toDatetime());
        assertEquals(announcementEntity.subject(), result.get().subject());
        assertEquals(announcementEntity.message(), result.get().message());

        Mockito.verify(announcementDao, times(1)).getByUuid(Mockito.eq(input));
    }

    @Test
    public void testGetNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(announcementDao.getByUuid(Mockito.any())).thenReturn(Optional.empty());

        var result = announcementService.getAnnouncement(input);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(announcementDao, times(1)).getByUuid(Mockito.eq(input));
    }

    @Test
    public void testGetAnnouncements() {
        var announcementOne = new AnnouncementEntity(10L, UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject one", "message one");
        var announcementTwo = new AnnouncementEntity(11L, UUID.randomUUID(), OffsetDateTime.now(), OffsetDateTime.now(), "subject two", "message two");

        Mockito.when(announcementDao.getAnnouncements(Mockito.any())).thenReturn(Arrays.asList(announcementOne, announcementTwo));

        var result = announcementService.getAnnouncements();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(announcementOne.message(), result.get(0).message());
        assertEquals(announcementOne.subject(), result.get(0).subject());
        assertEquals(announcementOne.toDatetime(), result.get(0).toDatetime());
        assertEquals(announcementOne.fromDatetime(), result.get(0).fromDatetime());
        assertEquals(announcementOne.uuid(), result.get(0).uuid());

        assertEquals(announcementTwo.message(), result.get(1).message());
        assertEquals(announcementTwo.subject(), result.get(1).subject());
        assertEquals(announcementTwo.toDatetime(), result.get(1).toDatetime());
        assertEquals(announcementTwo.fromDatetime(), result.get(1).fromDatetime());
        assertEquals(announcementTwo.uuid(), result.get(1).uuid());
    }
}
