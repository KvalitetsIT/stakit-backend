package dk.kvalitetsit.stakit.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GroupedServiceStatusDaoImplTest extends AbstractDaoTest {
    @Autowired
    private GroupedStatusDao groupedStatusDao;

    @Autowired
    private TestDataHelper testDataHelper;
    private long defaultGroupId;

    @Before
    public void setup() {
        defaultGroupId = testDataHelper.findDefaultGroupId();
    }

    @Test
    public void testGetGroupedStatusNothingFound() {
        var result = groupedStatusDao.getGroupedStatus();
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals("Default", result.get(0).groupName());
        assertNull(result.get(0).serviceName());
        assertNull(result.get(0).status());
    }


    @Test
    public void testGetGrouped() {
        var groupOne = testDataHelper.createGroup("Group One", UUID.randomUUID(), "Group Description One");

        var serviceConfigurationOne = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service One", false, groupOne, "OK", "Description One");
        var serviceConfigurationTwo = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service Two", false, groupOne, "OK", "Description Two");
        var serviceConfigurationThree = testDataHelper.createServiceConfiguration(UUID.randomUUID().toString(), "Service Three", false, defaultGroupId, "OK", "Description Three");

        testDataHelper.createServiceStatus(serviceConfigurationOne, "NOT_OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationOne, "OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationTwo, "NOT_OK", OffsetDateTime.now());
        testDataHelper.createServiceStatus(serviceConfigurationThree, "NOT_OK", OffsetDateTime.now());

        var result = groupedStatusDao.getGroupedStatus();
        assertNotNull(result);
        assertEquals(3, result.size());

        var groupedStatus = result.get(0);
        assertEquals("Service Three", groupedStatus.serviceName());
        assertEquals("Default", groupedStatus.groupName());
        assertEquals("NOT_OK", groupedStatus.status());
        assertEquals("Description Three", groupedStatus.serviceDescription());
        assertNull(groupedStatus.groupDescription());

        groupedStatus = result.get(1);
        assertEquals("Service One", groupedStatus.serviceName());
        assertEquals("Group One", groupedStatus.groupName());
        assertEquals("OK", groupedStatus.status());
        assertEquals("Description One", groupedStatus.serviceDescription());
        assertEquals("Group Description One", groupedStatus.groupDescription());

        groupedStatus = result.get(2);
        assertEquals("Service Two", groupedStatus.serviceName());
        assertEquals("Group One", groupedStatus.groupName());
        assertEquals("NOT_OK", groupedStatus.status());
        assertEquals("Description Two", groupedStatus.serviceDescription());
        assertEquals("Group Description One", groupedStatus.groupDescription());
    }
}
