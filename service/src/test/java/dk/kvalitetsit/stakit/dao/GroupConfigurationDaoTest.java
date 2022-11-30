package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupConfigurationDaoTest extends AbstractDaoTest {
    @Autowired
    private GroupConfigurationDao groupConfigurationDao;

    @Test
    public void testInsertAndGetAll() {
        var input = GroupConfigurationEntity.createInstance("group-name");

        var id = groupConfigurationDao.insert(input);

        var result = groupConfigurationDao.findAll();
        assertEquals(1, result.size());
        assertEquals(input.groupName(), result.get(0).groupName());
        assertEquals(id, result.get(0).id().longValue());
    }
}
