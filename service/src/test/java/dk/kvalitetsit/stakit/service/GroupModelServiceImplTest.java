package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.entity.GroupConfigurationEntity;
import dk.kvalitetsit.stakit.service.model.GroupModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class GroupModelServiceImplTest {
    private GroupConfigurationDao groupDao;
    private GroupService groupService;

    @Before
    public void setup() {
        groupDao = Mockito.mock(GroupConfigurationDao.class);
        groupService = new GroupServiceImpl(groupDao);
    }

    @Test
    public void testGetAllGroups() {
        var groupOne = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 1", 10);
        var groupTwo = new GroupConfigurationEntity(1L, UUID.randomUUID(), "name 2", 20);

        Mockito.when(groupDao.findAll()).thenReturn(Arrays.asList(groupOne, groupTwo));

        var result = groupService.getGroups();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(groupOne.name(), result.get(0).name());
        assertEquals(groupOne.uuid(), result.get(0).uuid());
        assertEquals(groupOne.name(), result.get(0).name());
        assertEquals(groupOne.displayOrder(), result.get(0).displayOrder());

        assertEquals(groupTwo.uuid(), result.get(1).uuid());
        assertEquals(groupTwo.name(), result.get(1).name());
        assertEquals(groupTwo.displayOrder(), result.get(1).displayOrder());
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
}
