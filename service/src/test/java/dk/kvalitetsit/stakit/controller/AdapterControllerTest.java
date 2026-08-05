package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.model.StatusUpdate;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class AdapterControllerTest {
    private AdapterController adapterController;
    private StatusUpdateService statusUpdateService;

    @BeforeEach
    public void setup() {
        statusUpdateService = Mockito.mock(StatusUpdateService.class);

        adapterController = new AdapterController(statusUpdateService);
    }

    @Test
    public void testStatusUpdate() {
        var input = new StatusUpdate();
        input.setService("service_id");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        var result = adapterController.v1StatusPost(input);

        assertNotNull(result);

        Mockito.verify(statusUpdateService, times(1)).updateStatus(Mockito.argThat(x -> {
            assertEquals(input.getMessage(), x.message());
            assertEquals(input.getService(), x.service());
            assertEquals(input.getStatusTime(), x.statusDateTime());
            assertEquals(Status.OK, x.status());

            return true;
        }));
    }
}
