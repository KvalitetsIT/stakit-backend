package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatusConfigurationDaoTest extends AbstractDaoTest {
    @Autowired
    private StatusConfigurationDao statusConfigurationDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsertWithGroup() {
        var groupId = testDataHelper.createGroup("group-name");
        var input = StatusConfigurationEntity.createInstance("Service", "service name", true, groupId);

        statusConfigurationDao.insert(input);

        var result = statusConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.serviceName(), entity.serviceName());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertEquals(groupId, result.get(0).groupConfigurationId().longValue());
    }

    @Test
    public void testInsertWithoutGroup() {
        var input = StatusConfigurationEntity.createInstance("Service", "service name", true, null);

        statusConfigurationDao.insert(input);

        var result = statusConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.serviceName(), entity.serviceName());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertNull(result.get(0).groupConfigurationId());
    }

    @Test
    public void testFindByService() {
        var service = "this_is_a_service";
        var serviceName = "service name";
        var id = testDataHelper.createServiceConfiguration(service, serviceName, true);

        var result = statusConfigurationDao.findByService(service);
        assertNotNull(result);
        assertEquals(id, result.id().longValue());
        assertEquals(service, result.service());
        assertEquals(serviceName, result.serviceName());
        assertTrue(result.ignoreServiceName());
    }

    @Test
    public void testFindByServiceNotFound() {
        var service = "this_is_a_service";

        var thrownException = assertThrows(EmptyResultDataAccessException.class, () -> statusConfigurationDao.findByService(service));
        assertNotNull(thrownException);
        assertEquals(0, thrownException.getActualSize());
        assertEquals(1, thrownException.getExpectedSize());
    }
}
