package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementDaoImplTest extends AbstractDaoTest {
    @Autowired
    private AnnouncementDao announcementDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsertAndGet() {
        var input = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "string message");

        var id = announcementDao.insert(input);

        var result = announcementDao.getByUuid(input.uuid());
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(id, result.get().id());
        assertEquals(input.fromDatetime(), result.get().fromDatetime());
        assertEquals(input.toDatetime(), result.get().toDatetime());
        assertEquals(input.subject(), result.get().subject());
        assertEquals(input.message(), result.get().message());
        assertEquals(input.uuid(), result.get().uuid());
    }

    @Test
    public void testUpdateAndGet() {
        var uuid = UUID.randomUUID();
        var fromDate = OffsetDateTime.now().minusDays(1);
        var toDate = OffsetDateTime.now();
        var subject = "subject";
        var message = "message";
        var id = testDataHelper.createAnnouncement(uuid, fromDate, toDate, subject, message);

        var updateInput = new AnnouncementEntity(20L, uuid, OffsetDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS), OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS), "updated subject", "update message");

        var updateResult = announcementDao.updateByUuid(updateInput);
        assertTrue(updateResult);

        var result = announcementDao.getByUuid(uuid);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(id, result.get().id());
        assertEquals(updateInput.fromDatetime(), result.get().fromDatetime());
        assertEquals(updateInput.toDatetime(), result.get().toDatetime());
        assertEquals(updateInput.subject(), result.get().subject());
        assertEquals(updateInput.message(), result.get().message());
        assertEquals(updateInput.uuid(), result.get().uuid());
    }

    @Test
    public void testUpdateNotFound() {
        var updateInput = new AnnouncementEntity(20L, UUID.randomUUID(), OffsetDateTime.now().minusDays(2), OffsetDateTime.now().minusDays(1), "updated subject", "update message");

        var updateResult = announcementDao.updateByUuid(updateInput);
        assertFalse(updateResult);
    }

    @Test
    public void testFindByUuidNotFound() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10, "description", true, true);

        var result = announcementDao.getByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDelete() {
        var input = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "string message");

        announcementDao.insert(input);

        var getResult = announcementDao.getByUuid(input.uuid());
        assertNotNull(getResult);
        assertTrue(getResult.isPresent());

        var result = announcementDao.deleteByUuid(input.uuid());
        assertTrue(result);

        getResult = announcementDao.getByUuid(input.uuid());
        assertNotNull(getResult);
        assertTrue(getResult.isEmpty());
    }

    @Test
    public void testDeleteNotFound() {
        var result = announcementDao.deleteByUuid(UUID.randomUUID());
        assertFalse(result);
    }

    @Test
    public void testGetAnnouncements() {
        var subject = "subject";
        var message = "message";
        var fromDate = OffsetDateTime.now().minusMinutes(10).truncatedTo(ChronoUnit.SECONDS);
        var toDate = OffsetDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.SECONDS);
        var uuid = UUID.randomUUID();

        testDataHelper.createAnnouncement(UUID.randomUUID(), OffsetDateTime.now().minusDays(2), OffsetDateTime.now().minusDays(1), "subject one", "message one");
        testDataHelper.createAnnouncement(uuid, fromDate, toDate, subject, message);
        testDataHelper.createAnnouncement(UUID.randomUUID(), OffsetDateTime.now().plusMinutes(11), OffsetDateTime.now().plusMinutes(20), "subject three", "message three");

        var result = announcementDao.getAnnouncements(OffsetDateTime.now());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(message, result.get(0).message());
        assertEquals(subject, result.get(0).subject());
        assertEquals(toDate, result.get(0).toDatetime());
        assertEquals(fromDate, result.get(0).fromDatetime());
        assertEquals(uuid, result.get(0).uuid());
    }

    @Test
    public void testAnnouncementNotFound() {
        var result = announcementDao.getById(Long.MAX_VALUE);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAnnouncementFound() {
        var announcementOne = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), "subject one", "message one");

        var idOne = announcementDao.insert(announcementOne);

        var result = announcementDao.getById(idOne);
        assertTrue(result.isPresent());
    }

    @Test
    public void testGetAllAnnouncements() {
        var announcementOne = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), "subject one", "message one");
        var announcementTwo = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), "subject one", "message one");

        announcementDao.insert(announcementOne);
        announcementDao.insert(announcementTwo);

        var result = announcementDao.getAnnouncements();
        assertEquals(2, result.size());

        assertEquals(
                List.of(announcementOne, announcementTwo),
                result.stream().map(x -> AnnouncementEntity.createInstance(
                        x.uuid(),
                        x.fromDatetime(),
                        x.toDatetime(),
                        x.subject(),
                        x.message()
                )).collect(Collectors.toList()));
    }

    @Test
    public void testUpdateAndGetAnnouncementsToSend() {
        var message = "message";
        var subject = "subject";
        var fromDate = OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS);
        var toDate = OffsetDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        var uuid = UUID.randomUUID();

        var announcementOne = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2), "subject one", "message one");
        var announcementTwo = AnnouncementEntity.createInstance(uuid, fromDate, toDate, subject, message);
        var announcementThree = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), "subject three", "message three");

        announcementDao.insert(announcementOne);
        announcementDao.insert(announcementTwo);
        announcementDao.insert(announcementThree);

        var updateToSent = announcementDao.updateAnnouncementToSent(announcementThree);
        assertTrue(updateToSent);

        var result = announcementDao.getAnnouncementsToSend();
        assertEquals(1, result.size());

        assertEquals(message, result.get(0).message());
        assertEquals(subject, result.get(0).subject());
        assertEquals(toDate, result.get(0).toDatetime());
        assertEquals(fromDate, result.get(0).fromDatetime());
        assertEquals(uuid, result.get(0).uuid());
    }
}
