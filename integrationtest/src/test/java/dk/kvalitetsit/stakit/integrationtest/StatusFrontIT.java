package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.ServiceStatus;
import org.openapitools.client.model.StatusUpdate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StatusFrontIT extends AbstractIntegrationTest {

    private final StaKitApi helloApi;

    public StatusFrontIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        helloApi = new StaKitApi(apiClient);
    }

    @Test
    public void testCallGetGroupedStatus() throws ApiException {
        var statusUpdate = new StatusUpdate();
        statusUpdate.setService(UUID.randomUUID().toString());
        statusUpdate.setServiceName("Service Name Test Status Get");
        statusUpdate.setStatus(StatusUpdate.StatusEnum.NOT_OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());

        helloApi.v1StatusPost(statusUpdate);

        statusUpdate.status(StatusUpdate.StatusEnum.OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());

        var result = helloApi.v1ServiceStatusGroupedGet();

        assertNotNull(result);
        assertNotNull(result.getStatusGroup());
        assertEquals(1, result.getStatusGroup().size());

        var group = result.getStatusGroup().get(0);
        assertEquals("Default", group.getGroupName());

        assertNotNull(group.getServices());
        assertTrue(group.getServices().size() >= 1);

        assertEquals(1L, group.getServices().stream().filter(x -> x.getServiceName().equals(statusUpdate.getServiceName())).count());
        assertEquals(ServiceStatus.StatusEnum.NOT_OK, group.getServices().stream().filter(x -> x.getServiceName().equals(statusUpdate.getServiceName())).findAny().get().getStatus());
    }
}
