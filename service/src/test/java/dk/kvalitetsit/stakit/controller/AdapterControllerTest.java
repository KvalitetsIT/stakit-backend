package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.GroupService;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Group;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.StatusElement;
import dk.kvalitetsit.stakit.service.model.StatusGrouped;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.GroupInput;
import org.openapitools.model.StatusUpdate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

public class AdapterControllerTest {
    private AdapterController adapterController;
    private StatusUpdateService statusUpdateService;

    @Before
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
