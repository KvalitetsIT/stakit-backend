package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntity;
import dk.kvalitetsit.stakit.dao.entity.ServiceConfigurationEntityWithGroupUuid;
import dk.kvalitetsit.stakit.service.model.GroupModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class GroupModelServiceImplTest {
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
        var groupOne = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 1", 10);
        var groupTwo = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 2", 20);

        var groupOneServices = new ArrayList<UUID>();
        var groupTwoServices = new ArrayList<UUID>();

        Mockito.when(groupDao.findAll()).thenReturn(Arrays.asList(groupOne, groupTwo));
        Mockito.when(serviceConfigurationDao.findByGroupUuid(groupOne.uuid())).thenReturn(groupOneServices);
        Mockito.when(serviceConfigurationDao.findByGroupUuid(groupTwo.uuid())).thenReturn(groupTwoServices);

        var result = groupService.getGroups();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(groupOne.name(), result.get(0).name());
        assertEquals(groupOne.uuid(), result.get(0).uuid());
        assertEquals(groupOne.name(), result.get(0).name());
        assertEquals(groupOne.displayOrder(), result.get(0).displayOrder());
        assertEquals(groupOneServices, result.get(0).services());

        assertEquals(groupTwo.uuid(), result.get(1).uuid());
        assertEquals(groupTwo.name(), result.get(1).name());
        assertEquals(groupTwo.displayOrder(), result.get(1).displayOrder());
        assertEquals(groupTwoServices, result.get(1).services());
    }

    @Test
    public void testCreateGroup() {
        var input = GroupModel.createInstance("name", 10);

        var result = groupService.createGroup(input);
        assertNotNull(result);

        Mockito.verify(groupDao, times(1)).insert(GroupConfigurationEntity.createInstance(result, input.name(), input.displayOrder()));
    }

    @Test
    public void testUpdateGroup() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder());

        Mockito.when(groupDao.update(daoInput)).thenReturn(true);

        var result = groupService.updateGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).update(daoInput);
    }

    @Test
    public void testUpdateGroupNotFound() {
        var input = new GroupModel(UUID.randomUUID(), "name", 10);

        var daoInput = GroupConfigurationEntity.createInstance(input.uuid(), input.name(), input.displayOrder());

        Mockito.when(groupDao.update(daoInput)).thenReturn(false);

        var result = groupService.updateGroup(input);
        assertFalse(result);

        Mockito.verify(groupDao, times(1)).update(daoInput);
    }

    @Test
    public void testDelete() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.delete(input)).thenReturn(true);

        var result = groupService.deleteGroup(input);
        assertTrue(result);

        Mockito.verify(groupDao, times(1)).delete(input);
    }

    @Test
    public void testDeleteNotFound() {
        var input = UUID.randomUUID();

        Mockito.when(groupDao.delete(input)).thenReturn(false);

        var result = groupService.deleteGroup(input);
        assertFalse(result);

        Mockito.verify(groupDao, times(1)).delete(input);
    }

    @Test
    public void testFindGroup() {
        var input = UUID.randomUUID();

        var services = new ArrayList<UUID>();

        var groupConfigurationEntity = new GroupConfigurationEntity(10L, input, "name", 10);
        Mockito.when(groupDao.findByUuid(input)).thenReturn(Optional.of(groupConfigurationEntity));
        Mockito.when(serviceConfigurationDao.findByGroupUuid(input)).thenReturn(services);

        var result = groupService.getGroup(input);
        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(groupConfigurationEntity.uuid(), result.get().uuid());
        assertEquals(groupConfigurationEntity.name(), result.get().name());
        assertEquals(groupConfigurationEntity.displayOrder(), result.get().displayOrder());
        assertEquals(services, result.get().services());

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
        var uuidGroup = UUID.randomUUID();

        var group = new GroupConfigurationEntity(10L, uuidGroup, "some name", 10);

        var uuidServiceAdd = UUID.randomUUID();
        var uuidServiceKeep = UUID.randomUUID();
        var uuidServiceDelete = UUID.randomUUID();

        List<UUID> oldServiceList = new ArrayList<>();
        List<UUID> updatedServiceList = new ArrayList<>();
        updatedServiceList.add(uuidServiceAdd);
        updatedServiceList.add(uuidServiceKeep);
        oldServiceList.add(uuidServiceKeep);
        oldServiceList.add(uuidServiceDelete);

        Mockito.when(serviceConfigurationDao.findByGroupUuid(uuidGroup)).thenReturn(oldServiceList);
        Mockito.when(serviceConfigurationDao.updateByUuid(any())).thenReturn(true);
        Mockito.when(groupDao.findByUuid(uuidGroup)).thenReturn(Optional.of(group));

        ServiceConfigurationEntityWithGroupUuid addService = new ServiceConfigurationEntityWithGroupUuid(10L, uuidServiceAdd, "add service", "add name", true, null, "description");
        ServiceConfigurationEntityWithGroupUuid keepService = new ServiceConfigurationEntityWithGroupUuid(10L, uuidServiceKeep, "keep service", "keep name", true, uuidGroup, "description");
        ServiceConfigurationEntityWithGroupUuid deleteService = new ServiceConfigurationEntityWithGroupUuid(10L, uuidServiceDelete, "delete service", "delete name", true, uuidGroup, "description");
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceAdd)).thenReturn(Optional.of(addService));
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceKeep)).thenReturn(Optional.of(keepService));
        Mockito.when(serviceConfigurationDao.findByUuidWithGroupUuid(uuidServiceDelete)).thenReturn(Optional.of(deleteService));

        var result = groupService.patchGroup(uuidGroup, updatedServiceList);
        assertTrue(result);
        
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(addService.id(), addService.uuid(), addService.service(), addService.name(), addService.ignoreServiceName(), group.id(), addService.description()));
        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(addService.id(), addService.uuid(), addService.service(), addService.name(), addService.ignoreServiceName(), groupDao.findDefaultGroupId(), addService.description()));

        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(deleteService.id(), deleteService.uuid(), deleteService.service(), deleteService.name(), deleteService.ignoreServiceName(), group.id(), deleteService.description()));
        Mockito.verify(serviceConfigurationDao, times(1)).updateByUuid(new ServiceConfigurationEntity(deleteService.id(), deleteService.uuid(), deleteService.service(), deleteService.name(), deleteService.ignoreServiceName(), groupDao.findDefaultGroupId(), deleteService.description()));

        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(keepService.id(), keepService.uuid(), keepService.service(), keepService.name(), keepService.ignoreServiceName(), group.id(), keepService.description()));
        Mockito.verify(serviceConfigurationDao, times(0)).updateByUuid(new ServiceConfigurationEntity(keepService.id(), keepService.uuid(), keepService.service(), keepService.name(), keepService.ignoreServiceName(), groupDao.findDefaultGroupId(), keepService.description()));

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
}
