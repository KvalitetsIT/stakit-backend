package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

public class GroupConfigurationDaoImplTest extends AbstractDaoTest {
    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testFindByIdAndDelete() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "group-name", 0, "description", true, true);

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findById(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(input.uuid(), result.get().uuid());
        assertEquals(input.name(), result.get().name());
        assertEquals(id, result.get().id().longValue());
        assertEquals(input.displayOrder(), result.get().displayOrder());
        assertEquals(input.description(), result.get().description());
        assertEquals(input.display(), result.get().display());
        assertEquals(input.expanded(), result.get().expanded());


        // Delete
        var deleted = groupConfigurationDao.delete(input.uuid());
        assertTrue(deleted);

        assertTrue(groupConfigurationDao.findById(id).isEmpty());
    }

    @Test
    public void testInsertAndGetAll() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "group-name", 0, "description", true, true);

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findAll();
        assertEquals(2, result.size());
        assertEquals(input.uuid(), result.get(0).uuid());
        assertEquals(input.name(), result.get(0).name());
        assertEquals(id, result.get(0).id().longValue());
        assertEquals(input.displayOrder(), result.get(0).displayOrder());
        assertEquals(input.description(), result.get(0).description());

        assertNotNull(result.get(1).uuid());
        assertEquals("Default", result.get(1).name());
        assertNotNull(result.get(1).id());
        assertEquals(10, result.get(1).displayOrder());
        assertNull(result.get(1).description());
    }

    @Test
    public void testInsertDefault() {

        var id = groupConfigurationDao.createDefaultGroup();

        var result = groupConfigurationDao.findById(id);
        assertTrue(result.isPresent());
        assertEquals("Default", result.get().name());
    }

    @Test
    public void testUpdate() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10, "description", true, true);

        var id = groupConfigurationDao.insert(group);

        var updatedGroup = new GroupConfigurationEntity(id, group.uuid(), "another name", 20, "another description", true, true);
        var result = groupConfigurationDao.update(updatedGroup);
        assertTrue(result);

        assertTrue(groupConfigurationDao.findAll().stream().anyMatch(updatedGroup::equals));
    }

    @Test
    public void testUpdateNotFound() {
        var groupUpdate = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "another name", 20, "another description", true, true);
        var result = groupConfigurationDao.update(groupUpdate);
        assertFalse(result);
    }

    @Test
    public void testFindByUuid() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10, "description", true, true);

        var groupId = groupConfigurationDao.insert(group);

        var result = groupConfigurationDao.findByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(groupId, result.get().id());
        assertEquals(group.name(), result.get().name());
        assertEquals(group.uuid(), result.get().uuid());
        assertEquals(group.displayOrder(), result.get().displayOrder());
        assertEquals(group.description(), result.get().description());

    }

    @Test
    public void testFindByUuidNotFound() {
        var group = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "name", 10, "description", true, true);

        var result = groupConfigurationDao.findByUuid(group.uuid());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteNotFound() {
        var result = groupConfigurationDao.delete(UUID.randomUUID());
        assertFalse(result);
    }

    @Test
    public void testDescriptionMayBeNull() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "Group", 10, null, true, true);
        groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findByUuid(input.uuid());
        assertNotNull(result);
        assertNull(result.get().description());
    }
}
