package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.ServiceStatusEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class ServiceStatusDaoImplTest extends AbstractDaoTest {
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
        var statusConfigurationId = testDataHelper.createServiceConfiguration("service", "service name",false, defaultGroupId);

        var input = ServiceStatusEntity.createInstance(statusConfigurationId,  "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS), "SOME MESSAGE");

        var serviceStatusId = serviceStatusDao.insert(input);

        var result = serviceStatusDao.findById(serviceStatusId);
        assertNotNull(result);
        assertTrue(result.isPresent());
        var entity = result.get();
        assertEquals(input.status(), entity.status());
        assertEquals(input.statusTime(), entity.statusTime());
        assertEquals(input.message(), entity.message());
        assertEquals(statusConfigurationId, entity.serviceConfigurationId().longValue());
        assertEquals(serviceStatusId, entity.id().longValue());
    }

    @Test
    public void testInsert() {
        var statusConfigurationId = testDataHelper.createServiceConfiguration("service", "service name", false, defaultGroupId);

        var input = ServiceStatusEntity.createInstance(statusConfigurationId,  "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS), "SOME MESSAGE");

        var serviceStatusId = serviceStatusDao.insert(input);

        var result = serviceStatusDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.status(), entity.status());
        assertEquals(input.statusTime(), entity.statusTime());
        assertEquals(input.message(), entity.message());
        assertEquals(statusConfigurationId, entity.serviceConfigurationId().longValue());
        assertEquals(serviceStatusId, result.get(0).id().longValue());
    }

    @Test
    public void testGetLatest() {
        var statusConfiguration = testDataHelper.createServiceConfiguration("service", "name", false, defaultGroupId);

        var now = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        testDataHelper.createServiceStatus(statusConfiguration, "OK", now);
        testDataHelper.createServiceStatus(statusConfiguration, "NOT_OK", now.minus(1, ChronoUnit.MICROS));

        var latest = serviceStatusDao.findLatest("service");
        assertNotNull(latest);
        assertTrue(latest.isPresent());

        assertEquals(statusConfiguration, latest.get().serviceConfigurationId().longValue());
        assertEquals("OK", latest.get().status());
        assertEquals(now, latest.get().statusTime());
        assertNull(latest.get().message());
    }
}
