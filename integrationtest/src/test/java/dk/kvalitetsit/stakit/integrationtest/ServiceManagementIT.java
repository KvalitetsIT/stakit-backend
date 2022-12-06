package dk.kvalitetsit.stakit.integrationtest;

import org.junit.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GroupManagementApi;
import org.openapitools.client.api.ServiceManagementApi;
import org.openapitools.client.model.GroupInput;
import org.openapitools.client.model.ServiceCreate;
import org.openapitools.client.model.ServiceUpdate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceManagementIT extends AbstractIntegrationTest {
    private final GroupManagementApi groupManagementApi;
    private final ServiceManagementApi serviceManagementApi;

    public ServiceManagementIT() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(getApiBasePath());

        groupManagementApi = new GroupManagementApi(apiClient);
        serviceManagementApi = new ServiceManagementApi(apiClient);
    }

    @Test
    public void testCreateServiceAndReadByUuid() throws ApiException {
        var groupInput = new GroupInput();
        groupInput.setName("group");
        groupInput.setDisplayOrder(10);;

        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getHeaders().get("Location").stream().findFirst().orElseThrow(RuntimeException::new);

        var input = new ServiceCreate();
        input.setServiceIdentifier("service");
        input.setName("name");
        input.setIgnoreServiceName(true);
        input.setGroup(UUID.fromString(groupUuid));

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        assertEquals(201, serviceResult.getStatusCode());
        var serviceUuid = serviceResult.getHeaders().get("Location").stream().findFirst().orElseThrow(RuntimeException::new);

        var result = serviceManagementApi.v1ServicesUuidGet(UUID.fromString(serviceUuid));
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
        groupInput.setDisplayOrder(10);;

        var groupResult = groupManagementApi.v1GroupsPostWithHttpInfo(groupInput);
        var groupUuid = groupResult.getHeaders().get("Location").stream().findFirst().orElseThrow(RuntimeException::new);

        var input = new ServiceCreate();
        input.setServiceIdentifier(UUID.randomUUID().toString());
        input.setName("name");
        input.setIgnoreServiceName(true);

        var serviceResult = serviceManagementApi.v1ServicesPostWithHttpInfo(input);
        assertEquals(201, serviceResult.getStatusCode());
        var serviceUuid = serviceResult.getHeaders().get("Location").stream().findFirst().orElseThrow(RuntimeException::new);

        var serviceUpdate = new ServiceUpdate();
        serviceUpdate.setServiceIdentifier("service updated");
        serviceUpdate.setName("name updated");
        serviceUpdate.setIgnoreServiceName(false);
        serviceUpdate.setGroup(UUID.fromString(groupUuid));

        var updateResult = serviceManagementApi.v1ServicesUuidPutWithHttpInfo(UUID.fromString(serviceUuid), serviceUpdate);
        assertEquals(201, updateResult.getStatusCode());

        var getAllResult = serviceManagementApi.v1ServicesGet();
        assertNotNull(getAllResult);

        var service = getAllResult.stream().filter(x -> x.getUuid().equals(UUID.fromString(serviceUuid))).findFirst().orElseThrow(RuntimeException::new);
        assertEquals(UUID.fromString(serviceUuid), service.getUuid());
        assertEquals(serviceUpdate.getServiceIdentifier(), service.getServiceIdentifier());
        assertEquals(serviceUpdate.getGroup(), service.getGroup());
        assertEquals(serviceUpdate.getIgnoreServiceName(), service.getIgnoreServiceName());
        assertEquals(serviceUpdate.getName(), service.getName());
    }
}
