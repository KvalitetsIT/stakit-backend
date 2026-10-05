package dk.kvalitetsit.stakit.integrationtest;

import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.api.ServiceManagementApi;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StatusHistoryIT extends AbstractIntegrationTest {
    private final StaKitApi staKitApi;
    private final ServiceManagementApi serviceManagementApi;
    private final AdapterApi adapterApi;

    public StatusHistoryIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        var adapterClient = new ApiClient();
        adapterClient.setBasePath(getApiBasePath());
        adapterClient.addDefaultHeader("X-API-KEY", ServiceStarter.API_KEY);

        var authenticatedApi = new ApiClient();
        authenticatedApi.setBasePath(getApiBasePath());
        authenticatedApi.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        staKitApi = new StaKitApi(apiClient);
        serviceManagementApi = new ServiceManagementApi(authenticatedApi);
        adapterApi = new AdapterApi(adapterClient);
    }

    @Test
    public void testGetStatusHistory() throws ApiException {
        var serviceIdentifier = UUID.randomUUID().toString();
        var serviceUuid = createService(serviceIdentifier);

        var now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        var from = now.minusDays(5);

        postStatus(serviceIdentifier, StatusUpdate.StatusEnum.OK, now.minusDays(10), "ok before");
        postStatus(serviceIdentifier, StatusUpdate.StatusEnum.NOT_OK, now.minusDays(3), "Database down");
        postStatus(serviceIdentifier, StatusUpdate.StatusEnum.NOT_OK, now.minusDays(2), "Still down");
        postStatus(serviceIdentifier, StatusUpdate.StatusEnum.PARTIAL_NOT_OK, now.minusDays(1), "Partially up");
        postStatus(serviceIdentifier, StatusUpdate.StatusEnum.OK, now.minusHours(1), null);

        var result = staKitApi.v1ServiceStatusHistoryUuidGet(serviceUuid, from);

        assertNotNull(result);
        assertEquals(4, result.size());

        assertPeriod(result.get(0), StatusPeriod.StatusEnum.OK, from, now.minusDays(3), "ok before");
        assertPeriod(result.get(1), StatusPeriod.StatusEnum.NOT_OK, now.minusDays(3), now.minusDays(1), "Database down");
        assertPeriod(result.get(2), StatusPeriod.StatusEnum.PARTIAL_NOT_OK, now.minusDays(1), now.minusHours(1), "Partially up");
        assertPeriod(result.get(3), StatusPeriod.StatusEnum.OK, now.minusHours(1), null, null);
    }

    @Test
    public void testGetStatusHistoryNoStatus() throws ApiException {
        var serviceUuid = createService(UUID.randomUUID().toString());

        var result = staKitApi.v1ServiceStatusHistoryUuidGet(serviceUuid, OffsetDateTime.now().minusDays(7));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetStatusHistoryNotFound() {
        var uuid = UUID.randomUUID();

        var expectedException = assertThrows(ApiException.class, () -> staKitApi.v1ServiceStatusHistoryUuidGet(uuid, OffsetDateTime.now().minusDays(7)));
        assertEquals(404, expectedException.getCode());

        var body = JSON.getGson().fromJson(expectedException.getResponseBody(), BasicError.class);
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), body.getStatusText());
        assertEquals("/v1/service-status-history/%s".formatted(uuid), body.getPath());
        assertEquals("Service with uuid %s not found".formatted(uuid), body.getError());
    }

    @Test
    public void testGetStatusHistoryTooFarBack() throws ApiException {
        var serviceUuid = createService(UUID.randomUUID().toString());

        var expectedException = assertThrows(ApiException.class, () -> staKitApi.v1ServiceStatusHistoryUuidGet(serviceUuid, OffsetDateTime.now().minusDays(365)));
        assertEquals(400, expectedException.getCode());

        var body = JSON.getGson().fromJson(expectedException.getResponseBody(), BasicError.class);
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("History can not be requested more than 90 days back.", body.getError());
    }

    private UUID createService(String serviceIdentifier) throws ApiException {
        var input = new ServiceCreate();
        input.setServiceIdentifier(serviceIdentifier);
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setDescription("description");

        return serviceManagementApi.v1ServicesPost(input).getUuid();
    }

    private void postStatus(String serviceIdentifier, StatusUpdate.StatusEnum status, OffsetDateTime statusTime, String message) throws ApiException {
        var statusUpdate = new StatusUpdate();
        statusUpdate.setService(serviceIdentifier);
        statusUpdate.setServiceName("name");
        statusUpdate.setStatus(status);
        statusUpdate.setStatusTime(statusTime);
        statusUpdate.setMessage(message);

        adapterApi.v1StatusPost(statusUpdate);
    }

    private void assertPeriod(StatusPeriod period, StatusPeriod.StatusEnum status, OffsetDateTime from, OffsetDateTime to, String message) {
        assertEquals(status, period.getStatus());
        assertEquals(from.toInstant(), period.getFrom().toInstant());
        if(to == null) {
            assertNull(period.getTo());
        }
        else {
            assertEquals(to.toInstant(), period.getTo().toInstant());
        }
        assertEquals(message, period.getMessage());
    }
}
