package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.StatusUpdate;

import java.time.OffsetDateTime;

public class StatusUpdateIT extends AbstractIntegrationTest {

    private final StaKitApi helloApi;

    public StatusUpdateIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        helloApi = new StaKitApi(apiClient);
    }

    @Test
    public void testCallService() throws ApiException {
        var input = new StatusUpdate();
        input.setService("service-id");
        input.setStatus(StatusUpdate.StatusEnum.OK);
        input.setStatusTime(OffsetDateTime.now());
        input.setMessage("Everything is OK.");

        // Fails if HTTP status code is not OK. // TODO Consider asserting expected status code.
        helloApi.v1StatusPost(input);
    }
}
