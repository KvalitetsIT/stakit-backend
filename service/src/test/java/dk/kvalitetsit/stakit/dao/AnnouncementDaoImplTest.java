package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.AnnouncementEntity;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
        var createInput = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "string message");

        var id = announcementDao.insert(createInput);

        var updateInput = new AnnouncementEntity(20L, createInput.uuid(), OffsetDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS), OffsetDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS), "updated subject", "update message");

        var updateResult = announcementDao.updateByUuid(updateInput);
        assertTrue(updateResult);

        var result = announcementDao.getByUuid(createInput.uuid());
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
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10, "description");

        var result = announcementDao.getByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDelete() {
        var input = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), "subject", "string message");

        var id = announcementDao.insert(input);

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
        var announcementOne = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1), "subject one", "message one");
        var announcementTwo = AnnouncementEntity.createInstance(UUID.randomUUID(), OffsetDateTime.now().minusDays(1), OffsetDateTime.now().minusMinutes(10), "subject two", "message two");

        var idOne = announcementDao.insert(announcementOne);
        var idTwo = announcementDao.insert(announcementTwo);

        var result = announcementDao.getAnnouncements(OffsetDateTime.now());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(announcementOne.message(), result.get(0).message());
        assertEquals(announcementOne.subject(), result.get(0).subject());
        assertEquals(announcementOne.toDatetime(), result.get(0).toDatetime());
        assertEquals(announcementOne.fromDatetime(), result.get(0).fromDatetime());
    }
}
