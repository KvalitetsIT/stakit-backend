package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.entity.GroupedStatus;
import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import dk.kvalitetsit.stakit.service.model.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;

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
        var result = statusGroupServiceImpl.getStatusGrouped();

        assertNotNull(result);
        assertEquals(1, result.size());
        var group = result.get(0);
        assertEquals("Default", group.groupName());
        assertTrue(group.status().isEmpty());

        Mockito.verify(groupStatusDao, times(1)).getGroupedStatus();
    }

    @Test
    public void testGetServices() {
        var groupOne = new GroupedStatus(null, "OK", "Service One", "Description One");
        var groupTwo = new GroupedStatus("Group One", "NOT_OK", "Service Two", "Description Two");
        var groupThree = new GroupedStatus("Group One", "OK", "Service Three", "Description Three");

        var dbResult = Arrays.asList(groupOne, groupTwo, groupThree);
        Mockito.when(groupStatusDao.getGroupedStatus()).thenReturn(dbResult);

        var result = statusGroupServiceImpl.getStatusGrouped();
        assertNotNull(result);
        assertEquals(2, result.size());

        var firstGroupResult = result.get(1);
        assertEquals("Default", firstGroupResult.groupName());
        assertEquals(1, firstGroupResult.status().size());
        assertEquals(Status.OK, firstGroupResult.status().get(0).status());
        assertEquals(groupOne.serviceName(), firstGroupResult.status().get(0).statusName());
        assertEquals(groupOne.description(), firstGroupResult.status().get(0).description());


        var secondGroupResult = result.get(0);
        assertEquals("Group One", secondGroupResult.groupName());
        assertEquals(2, secondGroupResult.status().size());

        assertEquals(Status.NOT_OK, secondGroupResult.status().get(0).status());
        assertEquals(groupTwo.serviceName(), secondGroupResult.status().get(0).statusName());
        assertEquals(groupTwo.description(), secondGroupResult.status().get(0).description());

        assertEquals(Status.OK, secondGroupResult.status().get(1).status());
        assertEquals(groupThree.serviceName(), secondGroupResult.status().get(1).statusName());
        assertEquals(groupThree.description(), secondGroupResult.status().get(1).description());
    }

    private ServiceStatusEntity createStatusEntity(String status, String message) {
        return new ServiceStatusEntity(1L, 1L, "OK", OffsetDateTime.now(), null);
    }
}
