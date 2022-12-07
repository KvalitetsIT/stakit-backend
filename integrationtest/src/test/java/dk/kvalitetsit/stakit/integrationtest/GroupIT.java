package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.model.GroupInput;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

public class GroupIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;

    public GroupIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        groupManagementApi = new GroupManagementApi(apiClient);
    }

    @Test
    public void testCreateUpdateAndGetGroup() throws ApiException {
        var groupUpdate = new GroupInput();
        groupUpdate.setName("name");
        groupUpdate.setDisplayOrder(20);

        var response = groupManagementApi.v1GroupsPostWithHttpInfo(groupUpdate);
        assertEquals(201, response.getStatusCode());

        var uuid = response.getData().getUuid();
        assertEquals(uuid.toString(), response.getHeaders().get("Location").get(0));

        groupUpdate.setDisplayOrder(10);
        groupUpdate.setName("name updated");
        groupManagementApi.v1GroupsUuidPut(uuid, groupUpdate);

        var result = groupManagementApi.v1GroupsGet();
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(x -> groupUpdate.getDisplayOrder().equals(x.getDisplayOrder()) &&
               groupUpdate.getName().equals(x.getName())));
    }
}
