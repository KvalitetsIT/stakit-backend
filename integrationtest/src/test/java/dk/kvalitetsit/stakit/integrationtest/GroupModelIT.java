package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.api.ServiceManagementApi;
import org.openapitools.client.model.GroupInput;
import org.openapitools.client.model.GroupPatch;
import org.openapitools.client.model.ServiceCreate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

public class GroupModelIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;
    private final ServiceManagementApi serviceManagementApi;

    public GroupModelIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        groupManagementApi = new GroupManagementApi(apiClient);
        serviceManagementApi = new ServiceManagementApi(apiClient);
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

    @Test
    public void testPatchGroup() throws ApiException {
        var group = new GroupInput();
        group.setName("patchName");
        group.setDisplayOrder(10);
        var groupPatch = groupManagementApi.v1GroupsPost(group);

        var groupUuid = groupPatch.getUuid();

        var input1 = new ServiceCreate();
        input1.setServiceIdentifier("patch service1");
        input1.setName("patch name1");
        input1.setIgnoreServiceName(true);
        input1.setGroup(groupUuid);
        input1.setDescription("patch description1");
        var service1 = serviceManagementApi.v1ServicesPost(input1);

        var input2 = new ServiceCreate();
        input2.setServiceIdentifier("patch service2");
        input2.setName("patch name2");
        input2.setIgnoreServiceName(true);
        input2.setGroup(groupUuid);
        input2.setDescription("patch description2");
        var service2 = serviceManagementApi.v1ServicesPost(input2);

        var input3 = new ServiceCreate();
        input3.setServiceIdentifier("patch service3");
        input3.setName("patch name3");
        input3.setIgnoreServiceName(true);
        input3.setGroup(null);
        input3.setDescription("patch description3");
        var service3 = serviceManagementApi.v1ServicesPost(input3);

        var patch = new GroupPatch();

        patch.addServicesItem(service2.getUuid());
        patch.addServicesItem(service3.getUuid());

        assertNotNull(groupPatch);

        var response = groupManagementApi.v1GroupsUuidPatchWithHttpInfo(groupUuid, patch);
        assertEquals(204, response.getStatusCode());

        var patchedGroup = groupManagementApi.v1GroupsUuidGet(groupUuid);
        var services = patchedGroup.getServices();
        assertEquals(2, services.size());
        assertFalse(services.stream().anyMatch(x ->  x.equals(service1.getUuid())));
        assertTrue(services.stream().anyMatch(x -> x.equals(service2.getUuid())));
        assertTrue(services.stream().anyMatch(x -> x.equals(service3.getUuid())));
    }
}
