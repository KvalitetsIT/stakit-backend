package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class StatusDaoTest extends AbstractDaoTest {
    @Autowired
    private StatusDao statusDao;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    public void testInsert() {
        var statusConfigurationId = testDataHelper.createServiceConfiguration("service", "service name",false);

        var input = StatusEntity.createInstance(statusConfigurationId,  "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS), "SOME MESSAGE");

        statusDao.insertUpdate(input);

        var result = statusDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.status(), entity.status());
        assertEquals(input.statusTime(), entity.statusTime());
        assertEquals(input.message(), entity.message());
        assertEquals(statusConfigurationId, entity.statusConfigurationId().longValue());
        assertNotNull(result.get(0).id());
    }
}
