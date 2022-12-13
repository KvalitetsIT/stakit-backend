package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GroupConfigurationDaoTest extends AbstractDaoTest {
    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testFindById() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "group-name", 0);

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findById(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(input.uuid(), result.get().uuid());
        assertEquals(input.name(), result.get().name());
        assertEquals(id, result.get().id().longValue());
        assertEquals(input.displayOrder(), result.get().displayOrder());
    }

    @Test
    public void testInsertAndGetAll() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "group-name", 0);

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findAll();
        assertEquals(1, result.size());
        assertEquals(input.uuid(), result.get(0).uuid());
        assertEquals(input.name(), result.get(0).name());
        assertEquals(id, result.get(0).id().longValue());
        assertEquals(input.displayOrder(), result.get(0).displayOrder());
    }

    @Test
    public void testUpdate() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10);

        var id = groupConfigurationDao.insert(group);

        var updatedGroup = new GroupConfigurationEntity(id, group.uuid(), "another name", 20);
        var result = groupConfigurationDao.update(updatedGroup);
        assertTrue(result);

        assertTrue(groupConfigurationDao.findAll().stream().anyMatch(updatedGroup::equals));
    }

    @Test
    public void testUpdateNotFound() {
        var groupUpdate = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "another name", 20);
        var result = groupConfigurationDao.update(groupUpdate);
        assertFalse(result);
    }

    @Test
    public void testFindByUuid() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10);

        var groupId = groupConfigurationDao.insert(group);

        var result = groupConfigurationDao.findByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(groupId, result.get().id());
        assertEquals(group.name(), result.get().name());
        assertEquals(group.uuid(), result.get().uuid());
        assertEquals(group.displayOrder(), result.get().displayOrder());
    }

    @Test
    public void testFindByUuidNotFound() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10);

        var result = groupConfigurationDao.findByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
