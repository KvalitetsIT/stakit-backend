package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;
import dk.kvalitetsit.stakit.service.model.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class StatusGroupServiceImplTest {
    private StatusGroupServiceImpl statusGroupServiceImpl;
    private GroupedStatusDao groupStatusDao;

    @Before
    public void setup() {
        groupStatusDao = Mockito.mock(GroupedStatusDao.class);

        statusGroupServiceImpl = new StatusGroupServiceImpl(groupStatusDao);
    }

    @Test
    public void testGetNoServices() {
        var daoResult = new GroupedStatus("Default", null, null, null, null);
        Mockito.when(groupStatusDao.getGroupedStatus()).thenReturn(Collections.singletonList(daoResult));

        var result = statusGroupServiceImpl.getStatusGrouped();

        assertNotNull(result);
        assertEquals(1, result.size());
        var group = result.get(0);
        assertEquals("Default", group.groupName());
        assertTrue(group.status().isEmpty());
        assertNull(group.description());

        Mockito.verify(groupStatusDao, times(1)).getGroupedStatus();
    }

    @Test
    public void testGetServices() {
        var groupOne = new GroupedStatus("Default", "OK", "Service One", "Group Description One", "Service Description One");
        var groupTwo = new GroupedStatus("Group One", "NOT_OK", "Service Two", "Group Description Two", "Service Description Two");
        var groupThree = new GroupedStatus("Group One", "OK", "Service Three", "Group Description Two", "Service Description Three");

        var dbResult = Arrays.asList(groupOne, groupTwo, groupThree);
        Mockito.when(groupStatusDao.getGroupedStatus()).thenReturn(dbResult);

        var result = statusGroupServiceImpl.getStatusGrouped();
        assertNotNull(result);
        assertEquals(2, result.size());

        var firstGroupResult = result.get(1);
        assertEquals("Default", firstGroupResult.groupName());
        assertEquals(1, firstGroupResult.status().size());
        assertEquals("Group Description One", firstGroupResult.description());
        assertEquals(Status.OK, firstGroupResult.status().get(0).status());
        assertEquals(groupOne.serviceName(), firstGroupResult.status().get(0).statusName());
        assertEquals(groupOne.serviceDescription(), firstGroupResult.status().get(0).description());


        var secondGroupResult = result.get(0);
        assertEquals("Group One", secondGroupResult.groupName());
        assertEquals(2, secondGroupResult.status().size());
        assertEquals("Group Description Two", secondGroupResult.description());

        assertEquals(Status.NOT_OK, secondGroupResult.status().get(0).status());
        assertEquals(groupTwo.serviceName(), secondGroupResult.status().get(0).statusName());
        assertEquals(groupTwo.serviceDescription(), secondGroupResult.status().get(0).description());

        assertEquals(Status.OK, secondGroupResult.status().get(1).status());
        assertEquals(groupThree.serviceName(), secondGroupResult.status().get(1).statusName());
        assertEquals(groupThree.serviceDescription(), secondGroupResult.status().get(1).description());
    }
}
