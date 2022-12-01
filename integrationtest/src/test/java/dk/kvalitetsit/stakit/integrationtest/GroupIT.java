package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StaKitApi;
import org.openapitools.client.model.GroupUpdate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GroupIT extends AbstractIntegrationTest {
    private final StaKitApi helloApi;

    public GroupIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        helloApi = new StaKitApi(apiClient);
    }

    @Test
    public void testCreateUpdateAndGetGroup() throws ApiException {
        var groupUpdate = new GroupUpdate();
        groupUpdate.setName("name");
        groupUpdate.setDisplayOrder(20);

        var response = helloApi.v1GroupPostWithHttpInfo(groupUpdate);
        assertEquals(201, response.getStatusCode());

        var uuid = response.getHeaders().get("Location").get(0);

        groupUpdate.setDisplayOrder(10);
        groupUpdate.setName("name updated");
        helloApi.v1GroupUuidPut(UUID.fromString(uuid), groupUpdate);

        var result = helloApi.v1GroupGet();
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(x -> {
            return groupUpdate.getDisplayOrder().equals(x.getDisplayOrder()) &&
                   groupUpdate.getName().equals(x.getName());

        }));
    }
}
