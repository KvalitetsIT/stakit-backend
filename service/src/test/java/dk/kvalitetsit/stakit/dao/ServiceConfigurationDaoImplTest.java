package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class    ServiceConfigurationDaoImplTest extends AbstractDaoTest {
    @Autowired
    private ServiceConfigurationDao serviceConfigurationDao;
    @Autowired
    private ServiceStatusDao serviceStatusDao;

    @Autowired
    private TestDataHelper testDataHelper;
    private long defaultGroupId;

    @Before
    public void setup() {
        defaultGroupId = testDataHelper.findDefaultGroupId();
    }

    @Test
    public void testFindById() {
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(),"service name", true, defaultGroupId, "OK", "description");

        var id = serviceConfigurationDao.insert(input);
        testDataHelper.createServiceStatus(id, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var result = serviceConfigurationDao.findById(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().id().longValue());
        assertEquals(input.groupConfigurationId(), result.get().groupConfigurationId());
        assertEquals(input.service(), result.get().service());
        assertEquals(input.uuid(), result.get().uuid());
        assertEquals(input.ignoreServiceName(), result.get().ignoreServiceName());
        assertEquals(input.name(), result.get().name());
        assertEquals(input.status(), result.get().status());
        assertEquals(input.description(), result.get().description());
    }

    @Test
    public void testInsertWithGroupAndDelete() {
        var groupId = testDataHelper.createGroup("group-name", UUID.randomUUID(), "group description");
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(), "service name", true, groupId, "OK", "description");

        var id = serviceConfigurationDao.insert(input);
        testDataHelper.createServiceStatus(id, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var result = serviceConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.name(), entity.name());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertEquals(groupId, result.get(0).groupConfigurationId().longValue());
        assertEquals(input.status(), entity.status());
        assertEquals(input.description(), entity.description());

        // Delete
        serviceStatusDao.deleteFromServiceConfigurationUuid(input.uuid());
        var deleted = serviceConfigurationDao.delete(input.uuid());
        assertTrue(deleted);

        assertTrue(serviceConfigurationDao.findById(id).isEmpty());
    }

    @Test
    public void testInsertWithoutGroup() {
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(),"service name", true, defaultGroupId, "OK", "description");

        var id = serviceConfigurationDao.insert(input);
        testDataHelper.createServiceStatus(id, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var result = serviceConfigurationDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.name(), entity.name());
        assertEquals(input.ignoreServiceName(), entity.ignoreServiceName());
        assertNotNull(result.get(0).id());
        assertEquals(defaultGroupId, result.get(0).groupConfigurationId().longValue());
        assertEquals(input.status(), entity.status());
        assertEquals(input.description(), entity.description());
    }

    @Test
    public void testFindByService() {
        var service = "this_is_a_service";
        var serviceName = "service name";
        var description = "description of service";
        var id = testDataHelper.createServiceConfiguration(service, serviceName, true, defaultGroupId, "OK", description);
        testDataHelper.createServiceStatus(id, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var result = serviceConfigurationDao.findByService(service);
        assertNotNull(result);
        assertEquals(id, result.id().longValue());
        assertEquals(service, result.service());
        assertEquals(serviceName, result.name());
        assertTrue(result.ignoreServiceName());
        assertEquals("OK", result.status());
        assertEquals(description, result.description());
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
        var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");

        var serviceId = testDataHelper.createServiceConfiguration("service", "service name", true, groupId, serviceUuid, "OK", "description");
        testDataHelper.createServiceStatus(serviceId, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var result = serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(serviceUuid, result.get().uuid());
        assertEquals(groupUuid, result.get().groupUuid());
        assertTrue(result.get().ignoreServiceName());
        assertEquals(serviceId, result.get().id().longValue());
        assertEquals("service name", result.get().name());
        assertEquals("service", result.get().service());
        assertEquals("description", result.get().description());
        assertEquals("OK", result.get().status());
    }

    @Test
    public void testUpdateByUuidNotFound() {
        var serviceEntity = new ServiceConfigurationEntity(10L, UUID.randomUUID(), "service", "name", true, defaultGroupId, "OK", "description");

        var updated = serviceConfigurationDao.updateByUuid(serviceEntity);
        assertFalse(updated);
    }

    @Test
    public void testUpdateByUuid() {
        var serviceUuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");

        var serviceId = testDataHelper.createServiceConfiguration("service", "service name", true, groupId, serviceUuid, "OK", "description");

        var input = new ServiceConfigurationEntity(serviceId, serviceUuid, "updated service", "updated name", false, groupId, "NOT_OK", "updated description");
        testDataHelper.createServiceStatus(serviceId, "NOT_OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

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
        assertEquals("updated description", updatedService.get().description());
        assertEquals("NOT_OK", updatedService.get().status());

    }

    @Test
    public void testFindAllWithGroupId() {
        var serviceOneUuid = UUID.randomUUID();
        var serviceTwoUuid = UUID.randomUUID();
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("group name", groupUuid, "group description");

        var serviceOneId = testDataHelper.createServiceConfiguration("service1", "service1 name", true, groupId, serviceOneUuid, "OK", "service1 description");
        var serviceTwoId = testDataHelper.createServiceConfiguration("service2", "service2 name", false, defaultGroupId, serviceTwoUuid, "NOT_OK", "service2 description");
        testDataHelper.createServiceStatus(serviceOneId, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        testDataHelper.createServiceStatus(serviceTwoId, "NOT_OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));


        var result = serviceConfigurationDao.findAllWithGroupId();
        assertNotNull(result);
        assertEquals(2, result.size());

        var firstService = result.get(0);
        assertEquals(serviceTwoUuid, firstService.uuid());
        assertNotNull(firstService.groupUuid());
        assertFalse(firstService.ignoreServiceName());
        assertEquals(serviceTwoId, firstService.id().longValue());
        assertEquals("service2", firstService.service());
        assertEquals("service2 name", firstService.name());
        assertEquals("service2 description", firstService.description());
        assertEquals("NOT_OK", firstService.status());

        var secondService = result.get(1);
        assertEquals(serviceOneUuid, secondService.uuid());
        assertEquals(groupUuid, secondService.groupUuid());
        assertTrue(secondService.ignoreServiceName());
        assertEquals(serviceOneId, secondService.id().longValue());
        assertEquals("service1", secondService.service());
        assertEquals("service1 name", secondService.name());
        assertEquals("service1 description", secondService.description());
        assertEquals("OK", secondService.status());
    }

    @Test
    public void testDeleteNotFound() {
        var result = serviceConfigurationDao.delete(UUID.randomUUID());
        assertFalse(result);
    }

    @Test
    public void testDescriptionMayBeNull() {
        var input = ServiceConfigurationEntity.createInstance("Service", UUID.randomUUID(),"service name", true, defaultGroupId, "OK", null);
        serviceConfigurationDao.insert(input);

        var result = serviceConfigurationDao.findByUuidWithGroupUuid(input.uuid());
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNull(result.get().description());
    }

    @Test
    public void testQueryForServiceByGroupUUID() {
        var groupUuid = UUID.randomUUID();
        var groupId = testDataHelper.createGroup("some group", groupUuid, "some description");

        var s1Uuid = UUID.randomUUID();
        var s2Uuid = UUID.randomUUID();

        var s1 = testDataHelper.createServiceConfiguration("s1", "n1", true, groupId, s1Uuid, "OK", null);
        var s2 = testDataHelper.createServiceConfiguration("s2", "n2", true, groupId, s2Uuid, null, null);

        testDataHelper.createServiceStatus(s1, "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var s1Entity = new ServiceConfigurationEntity(s1, s1Uuid, "s1", "n1", true, groupId, "OK", null);
        var s2Entity = new ServiceConfigurationEntity(s2, s2Uuid, "s2", "n2", true, groupId, null, null);

        var result = serviceConfigurationDao.findByGroupUuid(groupUuid);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(s1Entity));
        assertTrue(result.contains(s2Entity));
    }
}

