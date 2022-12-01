package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.model.StatusUpdate;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdapterIT extends AbstractIntegrationTest {

    private final AdapterApi adapterApi;

    public AdapterIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        adapterApi = new AdapterApi(apiClient);
    }

    @Test
    public void testCallService() throws ApiException {
        var input = new StatusUpdate();

        input.setService("service-id");
        input.setServiceName("This is a service name");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        // Fails if HTTP status code is not OK. // TODO Consider asserting expected status code.
        var result = adapterApi.v1StatusPostWithHttpInfo(input);
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode());
    }
}
