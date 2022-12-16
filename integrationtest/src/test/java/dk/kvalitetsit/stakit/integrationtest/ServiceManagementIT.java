package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.api.ServiceManagementApi;
import org.openapitools.client.model.BasicError;
import org.openapitools.client.model.GroupInput;
import org.openapitools.client.model.ServiceCreate;
import org.openapitools.client.model.ServiceUpdate;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceManagementIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;
    private final ServiceManagementApi serviceManagementApi;

    public ServiceManagementIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        groupManagementApi = new GroupManagementApi(apiClient);
        serviceManagementApi = new ServiceManagementApi(apiClient);
    }

    @Test
    public void testGetNotFound() {
        var uuid = UUID.randomUUID();
        var expectedException = assertThrows(ApiException.class, () -> serviceManagementApi.v1ServicesUuidGetWithHttpInfo(uuid));
        assertEquals(404, expectedException.getCode());

        var body = JSON.getGson().fromJson(expectedException.getResponseBody(), BasicError.class);
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), body.getStatusText());
        assertEquals("/v1/services/%s".formatted(uuid), body.getPath());
        assertEquals("Service with uuid %s not found".formatted(uuid), body.getError());
        assertNotNull(body.getTimestamp());
    }

    @Test
    public void testCreateServiceAndReadByUuid() throws ApiException {
        var groupInput = new GroupInput();
        groupInput.setName("group");
        groupInput.setDisplayOrder(10);

        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getData().getUuid();

        var input = new ServiceCreate();
        input.setServiceIdentifier("service");
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setGroup(groupUuid);

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        assertEquals(201, serviceResult.getStatusCode());
        var serviceUuid = serviceResult.getData().getUuid();
        assertEquals(serviceUuid.toString(), serviceResult.getHeaders().get("Location").get(0));

        var result = serviceManagementApi.v1ServicesUuidGet(serviceUuid);
        assertNotNull(result);
        assertEquals(input.getGroup(), result.getGroup());
        assertEquals(input.getServiceIdentifier(), result.getServiceIdentifier());
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getIgnoreServiceName(), result.getIgnoreServiceName());
    }

    @Test
    public void testUpdateAndGetAll() throws ApiException {
        var groupInput = new GroupInput();
        groupInput.setName("group");
        groupInput.setDisplayOrder(10);

        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getData().getUuid();

        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName("name");
        input.setIgnoreServiceName(true);

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        assertEquals(201, serviceResult.getStatusCode());
        var serviceUuid = serviceResult.getData().getUuid();
        assertEquals(serviceUuid.toString(), serviceResult.getHeaders().get("Location").get(0));

        var serviceUpdate = new ServiceUpdate();
        serviceUpdate.setServiceIdentifier("service updated");
        serviceUpdate.setName("name updated");
        serviceUpdate.setIgnoreServiceName(false);
        serviceUpdate.setGroup(groupUuid);

        var updateResult = serviceManagementApi.v1ServicesUuidPutWithHttpInfo(serviceUuid, serviceUpdate);
        assertEquals(201, updateResult.getStatusCode());

        var getAllResult = serviceManagementApi.v1ServicesGet();
        assertNotNull(getAllResult);

        var service = getAllResult.stream().filter(x -> x.getUuid().equals(serviceUuid)).findFirst().orElseThrow(RuntimeException::new);
        assertEquals(serviceUuid, service.getUuid());
        assertEquals(serviceUpdate.getServiceIdentifier(), service.getServiceIdentifier());
        assertEquals(serviceUpdate.getGroup(), service.getGroup());
        assertEquals(serviceUpdate.getIgnoreServiceName(), service.getIgnoreServiceName());
        assertEquals(serviceUpdate.getName(), service.getName());
    }

    @Test
    public void testPutNotFound() {
        var uuid = UUID.randomUUID();
        var input = new ServiceUpdate();
        input.setServiceIdentifier("id");
        input.setName("name");
        input.setIgnoreServiceName(true);

        var expectedException = assertThrows(ApiException.class, () -> serviceManagementApi.v1ServicesUuidPut(uuid, input));
        assertEquals(404, expectedException.getCode());

        var body = JSON.getGson().fromJson(expectedException.getResponseBody(), BasicError.class);
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), body.getStatusText());
        assertEquals("/v1/services/%s".formatted(uuid), body.getPath());
        assertEquals("Service with uuid %s not found".formatted(uuid), body.getError());
        assertNotNull(body.getTimestamp());
    }

    @Test
    public void testDeleteService() throws ApiException {
        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName("name");
        input.setIgnoreServiceName(true);

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        var serviceUuid = serviceResult.getData().getUuid();

        var result = serviceManagementApi.v1ServicesUuidDeleteWithHttpInfo(serviceUuid);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode());
    }
}
