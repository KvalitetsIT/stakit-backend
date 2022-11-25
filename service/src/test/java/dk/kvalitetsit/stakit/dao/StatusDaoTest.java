package dk.kvalitetsit.stakit.dao;

import dk.kvalitetsit.stakit.dao.entity.StatusEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;

public class StatusDaoTest extends AbstractDaoTest {
    @Autowired
    private StatusDao statusDao;

    @Test
    public void testByMessageId() {
        var input = StatusEntity.createInstance(UUID.randomUUID().toString(), "OK", OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS), "SOME MESSAGE");

        statusDao.insertUpdate(input);

        var result = statusDao.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        var entity = result.get(0);
        assertEquals(input.service(), entity.service());
        assertEquals(input.status(), entity.status());
        assertEquals(input.statusTime(), entity.statusTime());
        assertEquals(input.message(), entity.message());
        assertNotNull(result.get(0).id());
    }
}
