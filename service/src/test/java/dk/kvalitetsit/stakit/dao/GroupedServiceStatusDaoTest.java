package dk.kvalitetsit.stakit.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GroupedServiceStatusDaoTest extends AbstractDaoTest {
    @Autowired
    private GroupedStatusDao groupedStatusDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testGetGroupedStatusNothingFound() {
        var result = groupedStatusDao.getGroupedStatus();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGrouped() {
        var groupOne = testDataHelper.createGroup("Group One", UUID.randomUUID());

        var serviceConfigurationOne = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service One", false, groupOne);
        var serviceConfigurationTwo = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service Two", false, groupOne);

        var serviceConfigurationThree = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service Three", false);

        testDataHelper.createServiceStatus(serviceConfigurationOne, "NOT_OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationOne, "OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationTwo, "NOT_OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationThree, "NOT_OK", OffsetDateTime.now());

        var result = groupedStatusDao.getGroupedStatus();
        assertNotNull(result);
        assertEquals(3, result.size());

        var groupedStatus = result.get(0);
        assertEquals("Service Three", groupedStatus.serviceName());
        assertNull(groupedStatus.groupName());
        assertEquals("NOT_OK", groupedStatus.status());

        groupedStatus = result.get(1);
        assertEquals("Service One", groupedStatus.serviceName());
        assertEquals("Group One", groupedStatus.groupName());
        assertEquals("OK", groupedStatus.status());

        groupedStatus = result.get(2);
        assertEquals("Service Two", groupedStatus.serviceName());
        assertEquals("Group One", groupedStatus.groupName());
        assertEquals("NOT_OK", groupedStatus.status());
    }
}
