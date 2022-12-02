package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceConfigurationDaoTest extends AbstractDaoTest {
    @Autowired
    private ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsertWithGroup() {
        var groupId = testDataHelper.createGroup("group-name");
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(), "service name", true, groupId);

        serviceConfigurationDao.insert(input);

        var result = serviceConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.name(), entity.name());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertEquals(groupId, result.get(0).groupConfigurationId().longValue());
    }

    @Test
    public void testInsertWithoutGroup() {
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(),"service name", true, null);

        serviceConfigurationDao.insert(input);

        var result = serviceConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.name(), entity.name());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertNull(result.get(0).groupConfigurationId());
    }

    @Test
    public void testFindByService() {
        var service = "this_is_a_service";
        var serviceName = "service name";
        var id = testDataHelper.createServiceConfiguration(service, serviceName, true);

        var result = serviceConfigurationDao.findByService(service);
        assertNotNull(result);
        assertEquals(id, result.id().longValue());
        assertEquals(service, result.service());
        assertEquals(serviceName, result.name());
        assertTrue(result.ignoreServiceName());
    }

    @Test
    public void testFindByServiceNotFound() {
        var service = "this_is_a_service";

        var thrownException = assertThrows(EmptyResultDataAccessException.class, () -> serviceConfigurationDao.findByService(service));
        assertNotNull(thrownException);
        assertEquals(0, thrownException.getActualSize());
        assertEquals(1, thrownException.getExpectedSize());
    }
}
