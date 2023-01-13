package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class ServiceManagementServiceImplTest {
    private ServiceConfigurationDao serviceConfigurationDao;
    private ServiceManagementServiceImpl serviceManagementService;
    private GroupConfigurationDao groupConfigurationDao;

    @Before
    public void setup() {
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        groupConfigurationDao = Mockito.mock(GroupConfigurationDao.class);
        serviceManagementService = new ServiceManagementServiceImpl(serviceConfigurationDao, groupConfigurationDao);
    }

    @Test
    public void testGetService() {
        var serviceUuid = UUID.randomUUID();
        var serviceConfigurationEntity = new ServiceConfigurationEntityWithGroupUuid(1L, UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID());

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid)).thenReturn(Optional.of(serviceConfigurationEntity));

        var result = serviceManagementService.getService(serviceUuid);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(serviceConfigurationEntity.name(), result.get().name());
        assertEquals(serviceConfigurationEntity.service(), result.get().serviceIdentifier());
        assertEquals(serviceConfigurationEntity.groupUuid(), result.get().group());
        assertEquals(serviceConfigurationEntity.ignoreServiceName(), result.get().ignoreServiceName());

        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceUuid);
    }

    @Test
    public void testGetServiceNotFound() {
        var serviceUuid = UUID.randomUUID();

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceUuid)).thenReturn(Optional.empty());

        var result = serviceManagementService.getService(serviceUuid);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceUuid);
    }

    @Test
    public void testCreateService() {
        var groupId = 10L;
        var serviceCreate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), null);

        var groupConfigurationEntity = new GroupConfigurationEntity(groupId, UUID.randomUUID(), "group name", 10);
        Mockito.when(groupConfigurationDao.findByUuid(serviceCreate.group())).thenReturn(Optional.of(groupConfigurationEntity));

        var result = serviceManagementService.createService(serviceCreate);
        assertNotNull(result);

        Mockito.verify(serviceConfigurationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(serviceCreate.name(), x.name());
            assertEquals(serviceCreate.serviceIdentifier(), x.service());
            assertEquals(serviceCreate.ignoreServiceName(), x.ignoreServiceName());
            assertEquals(groupId, x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));
        Mockito.verify(groupConfigurationDao, times(1)).findByUuid(serviceCreate.group());
    }

    @Test
    public void testCreateServiceGroupNotFound() {
        var serviceCreate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), null);

        Mockito.when(groupConfigurationDao.findByUuid(serviceCreate.group())).thenReturn(Optional.empty());

        var result = serviceManagementService.createService(serviceCreate);
        assertNull(result);

        Mockito.verifyNoInteractions(serviceConfigurationDao);
        Mockito.verify(groupConfigurationDao, times(1)).findByUuid(serviceCreate.group());
    }

    @Test
    public void testCreateServiceNoGroup() {
        var groupId = 10L;

        Mockito.when(groupConfigurationDao.findDefaultGroupId()).thenReturn(groupId);

        var serviceCreate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, null, null);

        var result = serviceManagementService.createService(serviceCreate);
        assertNotNull(result);

        Mockito.verify(serviceConfigurationDao, times(1)).insert(Mockito.argThat(x -> {
            assertEquals(serviceCreate.name(), x.name());
            assertEquals(serviceCreate.serviceIdentifier(), x.service());
            assertEquals(serviceCreate.ignoreServiceName(), x.ignoreServiceName());
            assertEquals(groupId, x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));

        Mockito.verify(groupConfigurationDao, times(1)).findDefaultGroupId();
    }

    @Test
    public void testUpdateServiceNotFound() {
        var groupId = 10L;
        var serviceUuid = UUID.randomUUID();

        var serviceUpdate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, null, null);

        Mockito.when(serviceConfigurationDao.updateByUuid(Mockito.any())).thenReturn(false);
        Mockito.when(groupConfigurationDao.findDefaultGroupId()).thenReturn(groupId);

        var result = serviceManagementService.updateService(serviceUuid, serviceUpdate);
        assertFalse(result);

        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(Mockito.argThat(x -> {
            assertEquals(serviceUpdate.name(), x.name());
            assertEquals(serviceUpdate.serviceIdentifier(), x.service());
            assertEquals(serviceUpdate.ignoreServiceName(), x.ignoreServiceName());
            assertEquals(groupId, x.groupConfigurationId());
            assertEquals(serviceUuid, x.uuid());

            return true;
        }));
        Mockito.verify(groupConfigurationDao, times(1)).findDefaultGroupId();

    }

    @Test
    public void testUpdateService() {
        var groupId = 10L;
        var serviceUuid = UUID.randomUUID();
        var serviceUpdate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), null);

        var groupConfigurationEntity = new GroupConfigurationEntity(groupId, UUID.randomUUID(), "group name", 10);
        Mockito.when(groupConfigurationDao.findByUuid(serviceUpdate.group())).thenReturn(Optional.of(groupConfigurationEntity));

        Mockito.when(serviceConfigurationDao.updateByUuid(Mockito.any())).thenReturn(true);

        var result = serviceManagementService.updateService(serviceUuid, serviceUpdate);
        assertTrue(result);

        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(Mockito.argThat(x -> {
            assertEquals(serviceUpdate.name(), x.name());
            assertEquals(serviceUpdate.serviceIdentifier(), x.service());
            assertEquals(serviceUpdate.ignoreServiceName(), x.ignoreServiceName());
            assertEquals(groupId, x.groupConfigurationId());
            assertEquals(serviceUuid, x.uuid());

            return true;
        }));
        Mockito.verify(groupConfigurationDao, times(1)).findByUuid(serviceUpdate.group());
    }

    @Test
    public void testUpdateServiceGroupNotFound() {
        var serviceUuid = UUID.randomUUID();
        var serviceCreate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), null);

        Mockito.when(groupConfigurationDao.findByUuid(serviceCreate.group())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> serviceManagementService.updateService(serviceUuid, serviceCreate));

        Mockito.verifyNoInteractions(serviceConfigurationDao);
        Mockito.verify(groupConfigurationDao, times(1)).findByUuid(serviceCreate.group());
    }

    @Test
    public void testUpdateServiceNoGroup() {
        var serviceUuid = UUID.randomUUID();
        var serviceCreate = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, null, null);
        var groupId = 10L;

        Mockito.when(serviceConfigurationDao.updateByUuid(Mockito.any())).thenReturn(true);
        Mockito.when(groupConfigurationDao.findDefaultGroupId()).thenReturn(groupId);

        var result = serviceManagementService.updateService(serviceUuid, serviceCreate);
        assertTrue(result);

        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(Mockito.argThat(x -> {
            assertEquals(serviceCreate.name(), x.name());
            assertEquals(serviceCreate.serviceIdentifier(), x.service());
            assertEquals(serviceCreate.ignoreServiceName(), x.ignoreServiceName());
            assertEquals(groupId, x.groupConfigurationId());
            assertNotNull(x.uuid());

            return true;
        }));
        Mockito.verify(groupConfigurationDao, times(1)).findDefaultGroupId();
    }

    @Test
    public void testGetServices() {
        var serviceOne = new ServiceConfigurationEntityWithGroupUuid(10L, UUID.randomUUID(), "service 1", "service name 1", true, UUID.randomUUID());
        var serviceTwo = new ServiceConfigurationEntityWithGroupUuid(20L, UUID.randomUUID(), "service 2", "service name 2", false, null);

        Mockito.when(serviceConfigurationDao.findAllWithGroupId()).thenReturn(Arrays.asList(serviceOne, serviceTwo));

        var result = serviceManagementService.getServices();
        assertNotNull(result);
        assertEquals(2, result.size());

        var firstResult = result.get(0);
        assertEquals(serviceOne.name(), firstResult.name());
        assertEquals(serviceOne.ignoreServiceName(), firstResult.ignoreServiceName());
        assertEquals(serviceOne.service(), firstResult.serviceIdentifier());
        assertEquals(serviceOne.groupUuid(), firstResult.group());

        var secondResult = result.get(1);
        assertEquals(serviceTwo.name(), secondResult.name());
        assertEquals(serviceTwo.ignoreServiceName(), secondResult.ignoreServiceName());
        assertEquals(serviceTwo.service(), secondResult.serviceIdentifier());
        assertEquals(serviceTwo.groupUuid(), secondResult.group());
    }

    @Test
    public void testDelete() {
        var input = UUID.randomUUID();

        Mockito.when(serviceConfigurationDao.delete(input)).thenReturn(true);

        var result = serviceManagementService.deleteService(input);
        Assertions.assertTrue(result);

        Mockito.verify(serviceConfigurationDao, times(1)).delete(input);
    }

    @Test
    public void testDeleteNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(serviceConfigurationDao.delete(input)).thenReturn(false);

        var result = serviceManagementService.deleteService(input);
        assertFalse(result);

        Mockito.verify(serviceConfigurationDao, times(1)).delete(input);
    }
}
