package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.model.GroupInput;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GroupIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;

    public GroupIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        groupManagementApi = new GroupManagementApi(apiClient);
    }

    @Test
    public void testCreateUpdateAndGetGroup() throws ApiException {
        var groupUpdate = new GroupInput();
        groupUpdate.setName("name");
        groupUpdate.setDisplayOrder(20);

        var response = groupManagementApi.v1GroupsPostWithHttpInfo(groupUpdate);
        assertEquals(201, response.getStatusCode());

        var uuid = response.getHeaders().get("Location").get(0);

        groupUpdate.setDisplayOrder(10);
        groupUpdate.setName("name updated");
        groupManagementApi.v1GroupsUuidPut(UUID.fromString(uuid), groupUpdate);

        var result = groupManagementApi.v1GroupsGet();
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(x -> groupUpdate.getDisplayOrder().equals(x.getDisplayOrder()) &&
               groupUpdate.getName().equals(x.getName())));
    }
}
