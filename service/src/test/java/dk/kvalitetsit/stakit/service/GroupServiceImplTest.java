package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.exception.InvalidDataException;
import dk.kvalitetsit.stakit.service.model.GroupModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class GroupServiceImplTest {
    private GroupConfigurationDao groupDao;
    private GroupService groupService;
    private ServiceConfigurationDao serviceConfigurationDao;

    @Before
    public void setup() {
        groupDao = Mockito.mock(GroupConfigurationDao.class);
        serviceConfigurationDao = Mockito.mock(ServiceConfigurationDao.class);
        groupService = new GroupServiceImpl(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testGetAllGroups() {
        var groupOne = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 1", 10, "description 1", true);
        var groupTwo = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 2", 20, "description 2", true);

        var groupOneServices = new ArrayList<ServiceConfigurationEntity>();
        var groupTwoServices = new ArrayList<ServiceConfigurationEntity>();

        var serviceOne = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "Service in group one", "Name of service group one", true, groupOne.id(), "OK","descriptive one");
        var serviceTwo = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "Service in group two", "Name of service group two", true, groupTwo.id(), "OK", "descriptive two");

        groupOneServices.add(serviceOne);
        groupTwoServices.add(serviceTwo);

        Mockito.when(groupDao.findAll()).thenReturn(Arrays.asList(groupOne, groupTwo));
        Mockito.when(serviceConfigurationDao.findByGroupUuid(groupOne.uuid())).thenReturn(groupOneServices);
        Mockito.when(serviceConfigurationDao.findByGroupUuid(groupTwo.uuid())).thenReturn(groupTwoServices);

        var result = groupService.getGroups();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(groupOne.uuid(), result.get(0).uuid());
        assertEquals(groupOne.name(), result.get(0).name());
        assertEquals(groupOne.displayOrder(), result.get(0).displayOrder());
        assertEquals(groupOneServices.size(), result.get(0).services().size());
        assertEquals(groupOneServices.get(0).uuid(), result.get(0).services().get(0));
        assertEquals(groupOne.description(), result.get(0).description());

        assertEquals(groupTwo.uuid(), result.get(1).uuid());
        assertEquals(groupTwo.name(), result.get(1).name());
        assertEquals(groupTwo.displayOrder(), result.get(1).displayOrder());
        assertEquals(groupTwoServices.size(), result.get(1).services().size());
        assertEquals(groupTwoServices.get(0).uuid(), result.get(1).services().get(0));
        assertEquals(groupTwo.description(), result.get(1).description());
    }

    @Test
    public void testCreateGroup() {
        var input = GroupModel.createInstance("name", 10, "description", Collections.emptyList(), true);

        var result = groupService.createGroup(input);
        assertNotNull(result);

        Mockito.verify(groupDao, times(1)).insert(GroupConfigurationEntity.createInstance(result, input.name(), input.displayOrder(), input.description(), true));
    }

    @Test
    public void testCreateGroupWithServices() {
        var input = GroupModel.createInstance("name", 10, "description", Collections.singletonList(UUID.randomUUID()), true);

        var serviceConfiguration = new ServiceConfigurationEntityWithGroupUuid(11L, input.services().get(0), "service", "name", true, UUID.randomUUID(), null, "description");

        Mockito.when(groupDao.insert(Mockito.any())).thenReturn(1L);
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceConfiguration.uuid())).thenReturn(Optional.of(serviceConfiguration));

        var result = groupService.createGroup(input);
        assertNotNull(result);

        Mockito.verify(groupDao, times(1)).insert(GroupConfigurationEntity.createInstance(result, input.name(), input.displayOrder(), input.description(), true));
        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceConfiguration.uuid());
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(11L, serviceConfiguration.uuid(), serviceConfiguration.service(), serviceConfiguration.name(), true, 1L, null, serviceConfiguration.description()));

        Mockito.verifyNoMoreInteractions(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testCreateGroupWithServicesNotFound() {
        var input = GroupModel.createInstance("name", 10, "description", Collections.singletonList(UUID.randomUUID()), true);

        var serviceConfiguration = new ServiceConfigurationEntityWithGroupUuid(11L, input.services().get(0), "service", "name", true, UUID.randomUUID(), null, "description");

        GroupConfigurationEntity groupConfiguration = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name", 10, "description", true);

        Mockito.when(groupDao.insert(Mockito.any())).thenReturn(1L);
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceConfiguration.uuid())).thenReturn(Optional.empty());

        var result = assertThrows(InvalidDataException.class, () -> groupService.createGroup(input));
        assertNotNull(result);

        Mockito.verify(groupDao, times(1)).insert(Mockito.any());
        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceConfiguration.uuid());

        Mockito.verifyNoMoreInteractions(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testUpdateGroup() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10, "description", Collections.emptyList(), true);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder(), input.description(), true);

        Mockito.when(groupDao.update(daoInput)).thenReturn(true);

        var result = groupService.updateGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).update(daoInput);
    }

    @Test
    public void testUpdateGroupWithServices() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10, "description", Collections.singletonList(UUID.randomUUID()), true);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder(), input.description(), true);

        ServiceConfigurationEntityWithGroupUuid serviceConfiguration = new ServiceConfigurationEntityWithGroupUuid(10L, input.services().get(0), "service", "name", true, null, "OK", "description");

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceConfiguration.uuid())).thenReturn(Optional.of(serviceConfiguration));
        Mockito.when(groupDao.update(daoInput)).thenReturn(true);
        Mockito.when(groupDao.findByUuid(input.uuid())).thenReturn(Optional.of(new GroupConfigurationEntity(20L, input.uuid(), input.name(), input.displayOrder(), input.description(), true)));

        var result = groupService.updateGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).update(daoInput);
        Mockito.verify(groupDao, times(1)).findByUuid(input.uuid());
        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceConfiguration.uuid());
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(serviceConfiguration.id(), serviceConfiguration.uuid(), serviceConfiguration.service(), serviceConfiguration.name(), serviceConfiguration.ignoreServiceName(), 20L, null, "description"));

        Mockito.verifyNoMoreInteractions(serviceConfigurationDao, groupDao);
    }

    @Test
    public void testUpdateGroupWithServicesNotFound() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10, "description", Collections.singletonList(UUID.randomUUID()), true);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder(), input.description(), true);

        ServiceConfigurationEntityWithGroupUuid serviceConfiguration = new ServiceConfigurationEntityWithGroupUuid(10L, input.services().get(0), "service", "name", true, null, "OK", "description");

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(serviceConfiguration.uuid())).thenReturn(Optional.empty());
        Mockito.when(groupDao.update(daoInput)).thenReturn(true);
        Mockito.when(groupDao.findByUuid(input.uuid())).thenReturn(Optional.of(new GroupConfigurationEntity(20L, input.uuid(), input.name(), input.displayOrder(), input.description(), true)));

        var result = assertThrows(InvalidDataException.class, () -> groupService.updateGroup(input));
        assertNotNull(result);

        Mockito.verify(groupDao, times(1)).findByUuid(input.uuid());
        Mockito.verify(serviceConfigurationDao, times(1)).findByUuidWithGroupUuid(serviceConfiguration.uuid());

        Mockito.verifyNoMoreInteractions(serviceConfigurationDao, groupDao);
    }

    @Test
    public void testUpdateGroupNotFound() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10, "description", Collections.emptyList(), true);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder(), input.description(), true);

        Mockito.when(groupDao.update(daoInput)).thenReturn(false);

        var result = groupService.updateGroup(input);
        assertFalse(result);

        Mockito.verify(groupDao, times(1)).update(daoInput);
    }

    @Test
    public void testDelete() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.delete(input)).thenReturn(true);
        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(Collections.emptyList());
        Mockito.when(groupDao.findDefaultGroupId()).thenReturn(Optional.of(10L));

        var result = groupService.deleteGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).delete(input);
        Mockito.verify(groupDao, times(1)).findDefaultGroupId();
        Mockito.verify(serviceConfigurationDao, times(1)).findByGroupUuid(input);

        Mockito.verifyNoMoreInteractions(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testDeleteServicesMoved() {
        var input = UUID.randomUUID();

        var serviceConfigurationEntity = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", true, 10L, "OK", "desc");

        Mockito.when(groupDao.delete(input)).thenReturn(true);
        Mockito.when(groupDao.findDefaultGroupId()).thenReturn(Optional.of(11L));
        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(Collections.singletonList(serviceConfigurationEntity));

        var result = groupService.deleteGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).delete(input);
        Mockito.verify(groupDao, times(1)).findDefaultGroupId();
        Mockito.verify(serviceConfigurationDao, times(1)).findByGroupUuid(input);
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(serviceConfigurationEntity.id(), serviceConfigurationEntity.uuid(), serviceConfigurationEntity.service(), serviceConfigurationEntity.name(), serviceConfigurationEntity.ignoreServiceName(), 11L, null, serviceConfigurationEntity.description()));
        Mockito.verifyNoMoreInteractions(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testDeleteNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.delete(input)).thenReturn(false);
        Mockito.when(groupDao.findDefaultGroupId()).thenReturn(Optional.of(10L));

        var result = groupService.deleteGroup(input);
        assertFalse(result);

        Mockito.verify(groupDao, times(1)).delete(input);
        Mockito.verify(serviceConfigurationDao, times(1)).findByGroupUuid(input);
        Mockito.verify(groupDao, times(1)).findDefaultGroupId();

        Mockito.verifyNoMoreInteractions(groupDao, serviceConfigurationDao);
    }

    @Test
    public void testFindGroup() {
        var input = UUID.randomUUID();

        var services = new ArrayList<ServiceConfigurationEntity>();

        var groupConfigurationEntity = new GroupConfigurationEntity(10L, input, "name", 10, "description", true);
        var service = new ServiceConfigurationEntity(1L, UUID.randomUUID(), "service", "name", true, groupConfigurationEntity.id(), "OK","description");
        services.add(service);
        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.of(groupConfigurationEntity));
        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(services);

        var result = groupService.getGroup(input);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(groupConfigurationEntity.uuid(), result.get().uuid());
        assertEquals(groupConfigurationEntity.name(), result.get().name());
        assertEquals(groupConfigurationEntity.displayOrder(), result.get().displayOrder());
        assertEquals(services.size(), result.get().services().size());
        assertEquals(services.get(0).uuid(), result.get().services().get(0));
        assertEquals(groupConfigurationEntity.description(), result.get().description());

        Mockito.verify(groupDao, times(1)).findByUuid(input);
    }

    @Test
    public void testFindGroupNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.empty());

        var result = groupService.getGroup(input);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(groupDao, times(1)).findByUuid(input);
    }

    @Test
    public void testPatchGroup() {
        var defaultGroupId = 11L;

        var uuidGroup = UUID.randomUUID();

        var group = new GroupConfigurationEntity(10L, uuidGroup, "some name", 10, "some description", true);

        var uuidServiceAdd = UUID.randomUUID();
        var uuidServiceKeep = UUID.randomUUID();
        var uuidServiceDelete = UUID.randomUUID();

        List<ServiceConfigurationEntity> oldServiceList = new ArrayList<>();

        List<UUID> patchServiceList = new ArrayList<>();
        patchServiceList.add(uuidServiceAdd);
        patchServiceList.add(uuidServiceKeep);

        Mockito.when(serviceConfigurationDao.findByGroupUuid(uuidGroup)).thenReturn(oldServiceList);
        Mockito.when(serviceConfigurationDao.updateByUuid(any())).thenReturn(true);
        Mockito.when(groupDao.findByUuid(uuidGroup)).thenReturn(Optional.of(group));
        Mockito.when(groupDao.findDefaultGroupId()).thenReturn(Optional.of(defaultGroupId));

        ServiceConfigurationEntity addService = new ServiceConfigurationEntity(10L, uuidServiceAdd, "add service", "add name", true, null, "OK","description add");
        ServiceConfigurationEntity keepService = new ServiceConfigurationEntity(10L, uuidServiceKeep, "keep service", "keep name", true, group.id(), "OK", "description keep");
        ServiceConfigurationEntity deleteService = new ServiceConfigurationEntity(10L, uuidServiceDelete, "delete service", "delete name", true, group.id(), "OK", "description delete");
        oldServiceList.add(keepService);
        oldServiceList.add(deleteService);

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceAdd)).thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(addService.id(), addService.uuid(), addService.service(), addService.name(), addService.ignoreServiceName(), null, addService.status(), addService.description())));
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceKeep)).thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(keepService.id(), keepService.uuid(), keepService.service(), keepService.name(), keepService.ignoreServiceName(), uuidGroup, keepService.status(), keepService.description())));
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceDelete)).thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(deleteService.id(), deleteService.uuid(), deleteService.service(), deleteService.name(), deleteService.ignoreServiceName(), uuidGroup, deleteService.status(), deleteService.description())));

        var result = groupService.patchGroup(uuidGroup, patchServiceList);
        assertTrue(result);
        
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(addService.id(), addService.uuid(), addService.service(), addService.name(), addService.ignoreServiceName(), group.id(), addService.status(), addService.description()));
        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(addService.id(), addService.uuid(), addService.service(), addService.name(), addService.ignoreServiceName(), defaultGroupId, addService.status(), addService.description()));

        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(deleteService.id(), deleteService.uuid(), deleteService.service(), deleteService.name(), deleteService.ignoreServiceName(), group.id(), deleteService.status(), deleteService.description()));
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(deleteService.id(), deleteService.uuid(), deleteService.service(), deleteService.name(), deleteService.ignoreServiceName(), defaultGroupId, deleteService.status(), deleteService.description()));

        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(keepService.id(), keepService.uuid(), keepService.service(), keepService.name(), keepService.ignoreServiceName(), group.id(), keepService.status(), keepService.description()));
        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(keepService.id(), keepService.uuid(), keepService.service(), keepService.name(), keepService.ignoreServiceName(), defaultGroupId, keepService.status(), keepService.description()));

    }

    @Test
    public void testPatchGroupNotFound() {
        var inputUuid = UUID.randomUUID();
        List<UUID> inputList = new ArrayList<>();

        Mockito.when(groupDao.findByUuid(inputUuid)).thenReturn(Optional.empty());

        var result = groupService.patchGroup(inputUuid, inputList);
        assertFalse(result);

        Mockito.verify(groupDao, times(1)).findByUuid(inputUuid);

    }

    @Test
    public void testPatchGroupServiceNotFound() {
        var uuidGroup = UUID.randomUUID();

        var group = new GroupConfigurationEntity(10L, uuidGroup, "some name", 10, "description", true);

        var uuidService = UUID.randomUUID();

        List<UUID> serviceList = new ArrayList<>();
        serviceList.add(uuidService);

        Mockito.when(groupDao.findByUuid(uuidGroup)).thenReturn(Optional.of(group));

        var expectedException = assertThrows(InvalidDataException.class, () -> groupService.patchGroup(uuidGroup, serviceList));
        assertEquals("Service not found: %s".formatted(uuidService), expectedException.getMessage());

        Mockito.verify(groupDao, times(1)).findByUuid(uuidGroup);
        Mockito.verify(serviceConfigurationDao, times(0)).findByGroupUuid(uuidService);
        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(any());
    }

    @Test
    public void testGetServicesInGroup() {
        var input = UUID.randomUUID();

        var services = new ArrayList<ServiceConfigurationEntity>();

        var groupConfigurationEntity = new GroupConfigurationEntity(10L, input, "name", 10, "description", true);

        var uuidService1 = UUID.randomUUID();
        var uuidService2 = UUID.randomUUID();

        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(services);
        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.of(groupConfigurationEntity));

        ServiceConfigurationEntity service1 = new ServiceConfigurationEntity(10L, uuidService1, "service 1", "name 1", true, groupConfigurationEntity.id(), "OK","description 1");
        ServiceConfigurationEntity service2 = new ServiceConfigurationEntity(10L, uuidService2, "service 2", "name 2", true, groupConfigurationEntity.id(), "OK","description 2");

        services.add(service1);
        services.add(service2);

        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidService1)).thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(10L, uuidService1, "service 1", "name 1", true, input, "OK", "description 1")));
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidService2)).thenReturn(Optional.of(new ServiceConfigurationEntityWithGroupUuid(10L, uuidService2, "service 2", "name 2", true, input, "OK" ,"description 2")));

        var result = groupService.getServicesInGroup(input);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.get().size());

        assertEquals(uuidService1, result.get().get(0).uuid());
        assertEquals(service1.name(), result.get().get(0).name());
        assertEquals(service1.service(), result.get().get(0).serviceIdentifier());
        assertEquals(service1.ignoreServiceName(), result.get().get(0).ignoreServiceName());
        assertEquals(input, result.get().get(0).group());
        assertEquals(service1.status(), result.get().get(0).status());
        assertEquals(service1.description(), result.get().get(0).description());

        assertEquals(uuidService2, result.get().get(1).uuid());
        assertEquals(service2.name(), result.get().get(1).name());
        assertEquals(service2.service(), result.get().get(1).serviceIdentifier());
        assertEquals(service2.ignoreServiceName(), result.get().get(1).ignoreServiceName());
        assertEquals(input, result.get().get(1).group());
        assertEquals(service2.status(), result.get().get(1).status());
        assertEquals(service2.description(), result.get().get(1).description());
    }

    @Test
    public void testGetServicesInGroupNoServicesInGroup() {
        var input = UUID.randomUUID();

        var services = new ArrayList<ServiceConfigurationEntity>();

        var groupConfigurationEntity = new GroupConfigurationEntity(10L, input, "name", 10, "description", true);

        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(services);
        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.of(groupConfigurationEntity));

        var result = groupService.getServicesInGroup(input);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(0, result.get().size());
    }

    @Test
    public void testGetServicesInGroupNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.empty());

        var result = groupService.getServicesInGroup(input);
        assertTrue(result.isEmpty());
        Mockito.verify(groupDao, times(1)).findByUuid(input);
        Mockito.verify(serviceConfigurationDao, times(0)).findByGroupUuid(any());
    }
}
