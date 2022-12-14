package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceConfigurationDaoImplTest extends AbstractDaoTest {
    @Autowired
    private ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testFindById() {
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(),"service name", true, null);

        var id = serviceConfigurationDao.insert(input);

        var result = serviceConfigurationDao.findById(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().id().longValue());
        assertEquals(input.groupConfigurationId(), result.get().groupConfigurationId());
        assertEquals(input.service(), result.get().service());
        assertEquals(input.uuid(), result.get().uuid());
        assertEquals(input.ignoreServiceName(), result.get().ignoreServiceName());
        assertEquals(input.name(), result.get().name());
    }

    @Test
    public void testInsertWithGroupAndDelete() {
        var groupId = testDataHelper.createGroup("group-name", UUID.randomUUID());
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(), "service name", true, groupId);

        var id = serviceConfigurationDao.insert(input);

        var result = serviceConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.name(), entity.name());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertEquals(groupId, result.get(0).groupConfigurationId().longValue());

        // Delete
        var deleted = serviceConfigurationDao.delete(input.uuid());
        assertTrue(deleted);

        assertTrue(serviceConfigurationDao.findById(id).isEmpty());
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

    @Test
    public void testFindByUuidWithGroupUuidNotFound() {
        var result = serviceConfigurationDao.findByUuidWithGroupUuid(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByUuidWithGroupUuid() {
        var serviceUuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("group name", groupUuid);

        var serviceId = testDataHelper.createServiceConfiguration("service", "service name", true, groupId, serviceUuid);

        var result = serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(serviceUuid, result.get().uuid());
        assertEquals(groupUuid, result.get().groupUuid());
        assertTrue(result.get().ignoreServiceName());
        assertEquals(serviceId, result.get().id().longValue());
        assertEquals("service name", result.get().name());
        assertEquals("service", result.get().service());
    }

    @Test
    public void testUpdateByUuidNotFound() {
        var serviceEntity = new ServiceConfigurationEntity(10L, UUID.randomUUID(), "service", "name", true, null);

        var updated = serviceConfigurationDao.updateByUuid(serviceEntity);
        assertFalse(updated);
    }

    @Test
    public void testUpdateByUuid() {
        var serviceUuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("group name", groupUuid);

        var serviceId = testDataHelper.createServiceConfiguration("service", "service name", true, groupId, serviceUuid);

        var input = new ServiceConfigurationEntity(serviceId, serviceUuid, "updated service", "updated name", false, groupId);

        var result = serviceConfigurationDao.updateByUuid(input);
        assertTrue(result);

        var updatedService = serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid);
        assertNotNull(updatedService);
        assertTrue(updatedService.isPresent());

        assertEquals(serviceUuid, updatedService.get().uuid());
        assertEquals(groupUuid, updatedService.get().groupUuid());
        assertFalse(updatedService.get().ignoreServiceName());
        assertEquals(serviceId, updatedService.get().id().longValue());
        assertEquals("updated name", updatedService.get().name());
        assertEquals("updated service", updatedService.get().service());

    }

    @Test
    public void testFindAllWithGroupId() {
        var serviceOneUuid = UUID.randomUUID();
        var serviceTwoUuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("group name", groupUuid);

        var serviceOneId = testDataHelper.createServiceConfiguration("service1", "service1 name", true, groupId, serviceOneUuid);
        var serviceTwoId = testDataHelper.createServiceConfiguration("service2", "service2 name", false, null, serviceTwoUuid);


        var result = serviceConfigurationDao.findAllWithGroupId();
        assertNotNull(result);
        assertEquals(2, result.size());

        var firstService = result.get(0);
        assertEquals(serviceTwoUuid, firstService.uuid());
        assertNull(firstService.groupUuid());
        assertFalse(firstService.ignoreServiceName());
        assertEquals(serviceTwoId, firstService.id().longValue());
        assertEquals("service2", firstService.service());
        assertEquals("service2 name", firstService.name());

        var secondService = result.get(1);
        assertEquals(serviceOneUuid, secondService.uuid());
        assertEquals(groupUuid, secondService.groupUuid());
        assertTrue(secondService.ignoreServiceName());
        assertEquals(serviceOneId, secondService.id().longValue());
        assertEquals("service1", secondService.service());
        assertEquals("service1 name", secondService.name());
    }

    @Test
    public void testDeleteNotFound() {
        var result = serviceConfigurationDao.delete(UUID.randomUUID());
        assertFalse(result);
    }
}

