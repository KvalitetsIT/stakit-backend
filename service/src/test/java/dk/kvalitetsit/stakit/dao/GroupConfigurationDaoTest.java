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
    public void testInsertAndGetAll() {
        var input = GroupConfigurationEntity.createInstance(UUID.randomUUID(), "group-name", 0);

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findAll();
        assertEquals(1, result.size());
        assertEquals(input.uuid(), result.get(0).uuid());
        assertEquals(input.groupName(), result.get(0).groupName());
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
}
