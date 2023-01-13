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

public class GroupModelIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;

    public GroupModelIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
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

    @Test
    public void testGetSingleGroup() throws ApiException {
        var groupUpdate = new GroupInput();
        groupUpdate.setName("name");
        groupUpdate.setDisplayOrder(20);

        var response = groupManagementApi.v1GroupsPost(groupUpdate);

        var result = groupManagementApi.v1GroupsUuidGet(response.getUuid());
        assertNotNull(result);
        assertEquals(groupUpdate.getName(), result.getName());
        assertEquals(groupUpdate.getDisplayOrder(), result.getDisplayOrder());
        assertEquals(response.getUuid(), result.getId());
    }

    @Test
    public void testDeleteGroup() throws ApiException {
        var groupUpdate = new GroupInput();
        groupUpdate.setName("name");
        groupUpdate.setDisplayOrder(20);

        var createResponse = groupManagementApi.v1GroupsPostWithHttpInfo(groupUpdate);
        var uuid = createResponse.getData().getUuid();

        var response = groupManagementApi.v1GroupsUuidDeleteWithHttpInfo(uuid);
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void testGetAll() throws ApiException {
        var groups = groupManagementApi.v1GroupsGet();

        assertNotNull(groups);

        assertTrue(groups.size() > 0);
        assertTrue(groups.stream().anyMatch(x -> x.getName().equals("Default")));
    }
}
