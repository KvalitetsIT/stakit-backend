package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.JSON;
import org.openapitools.client.api.AdapterApi;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.api.ServiceManagementApi;
import org.openapitools.client.model.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceManagementIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;
    private final ServiceManagementApi serviceManagementApi;
    private final AdapterApi adapterApi;

    public ServiceManagementIT() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());
        apiClient.addDefaultHeader("Authorization", "Bearer " + generateSignedToken());

        var adapterClient = new ApiClient();
        adapterClient.setBasePath(getApiBasePath());
        adapterClient.addDefaultHeader("X-API-KEY", ServiceStarter.API_KEY);

        groupManagementApi = new GroupManagementApi(apiClient);
        serviceManagementApi = new ServiceManagementApi(apiClient);
        adapterApi = new AdapterApi(adapterClient);
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
        groupInput.display(true);

        groupInput.setExpanded(true);

        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getData().getUuid();

        var input = new ServiceCreate();
        input.setServiceIdentifier("service");
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setGroup(groupUuid);
        input.setDescription("description");

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
        assertNull(result.getStatus());
        assertEquals(input.getDescription(), result.getDescription());
    }

    @Test
    public void testUpdateAndGetAll() throws ApiException {
        var groupInput = new GroupInput();
        groupInput.setName(UUID.randomUUID().toString());
        groupInput.setDisplayOrder(10);
        groupInput.setDisplay(true);

        groupInput.setExpanded(true);
        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getData().getUuid();

        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName(UUID.randomUUID().toString());
        input.setIgnoreServiceName(true);
        input.setDescription("description");

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        assertEquals(201, serviceResult.getStatusCode());
        var serviceUuid = serviceResult.getData().getUuid();
        assertEquals(serviceUuid.toString(), serviceResult.getHeaders().get("Location").get(0));

        var serviceUpdate = new ServiceUpdate();
        serviceUpdate.setServiceIdentifier("service updated");
        serviceUpdate.setName("name updated");
        serviceUpdate.setIgnoreServiceName(false);
        serviceUpdate.setGroup(groupUuid);
        serviceUpdate.setDescription("description updated");

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
        assertEquals(serviceUpdate.getDescription(), service.getDescription());
    }

    @Test
    public void testPutNotFound() {
        var uuid = UUID.randomUUID();
        var input = new ServiceUpdate();
        input.setServiceIdentifier("id");
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setDescription("description");

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
        input.setDescription("description");

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        var serviceUuid = serviceResult.getData().getUuid();

        var result = serviceManagementApi.v1ServicesUuidDeleteWithHttpInfo(serviceUuid);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode());
    }

    @Test
    public void testCreateAndDeleteServiceWithStatus() throws ApiException {
        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setDescription("description");

        var statusUpdate = new StatusUpdate();
        statusUpdate.setService(input.getServiceIdentifier());
        statusUpdate.setServiceName(input.getName());
        statusUpdate.setStatus(StatusUpdate.StatusEnum.OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        var serviceUuid = serviceResult.getData().getUuid();
        adapterApi.v1StatusPost(statusUpdate);
        var result = serviceManagementApi.v1ServicesUuidDeleteWithHttpInfo(serviceUuid);
        assertNotNull(result);
        assertEquals(204, result.getStatusCode());
    }

    @Test
    public void testChangeStatus() throws ApiException {
        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setDescription("description");

        var statusUpdate = new StatusUpdate();
        statusUpdate.setService(input.getServiceIdentifier());
        statusUpdate.setServiceName(input.getName());

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        var serviceUuid = serviceResult.getData().getUuid();

        ApiResponse<Service> result;

        // OK
        statusUpdate.setStatus(StatusUpdate.StatusEnum.OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());
        adapterApi.v1StatusPost(statusUpdate);
        result = serviceManagementApi.v1ServicesUuidGetWithHttpInfo(serviceUuid);
        assertEquals(Service.StatusEnum.OK, result.getData().getStatus());

        // PARTIAL_NOT_OK
        statusUpdate.setStatus(StatusUpdate.StatusEnum.PARTIAL_NOT_OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());
        adapterApi.v1StatusPost(statusUpdate);
        result = serviceManagementApi.v1ServicesUuidGetWithHttpInfo(serviceUuid);
        assertEquals(Service.StatusEnum.PARTIAL_NOT_OK, result.getData().getStatus());

        // NOT_OK
        statusUpdate.setStatus(StatusUpdate.StatusEnum.NOT_OK);
        statusUpdate.setStatusTime(OffsetDateTime.now());
        adapterApi.v1StatusPost(statusUpdate);
        result = serviceManagementApi.v1ServicesUuidGetWithHttpInfo(serviceUuid);
        assertEquals(Service.StatusEnum.NOT_OK, result.getData().getStatus());
    }




}
