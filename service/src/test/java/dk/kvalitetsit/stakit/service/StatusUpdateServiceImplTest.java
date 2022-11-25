package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatusUpdateServiceImplTest {
    private StatusUpdateService statusUpdateService;

    @Before
    public void setup() {
        statusUpdateService = new StatusUpdateServiceImpl();
    }

    @Test
    public void testValidInput() {
        var input = new UpdateServiceInput(UUID.randomUUID().toString(), Status.NOT_OK, OffsetDateTime.now(), "Some message");

        statusUpdateService.updateStatus(input);
    }
}
