package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.service.ServiceManagementService;
import dk.kvalitetsit.stakit.service.model.ServiceModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class ServiceModelManagementControllerTest {
    private ServiceManagementService serviceManagementService;
    private ServiceManagementController serviceManagementController;

    @Before
    public void setup() {
        serviceManagementService = Mockito.mock(ServiceManagementService.class);
        serviceManagementController = new ServiceManagementController(serviceManagementService);
    }

    @Test
    public void testGetAllServices() {
        var serviceOne = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID().toString());
        var serviceTwo = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, null, UUID.randomUUID(), UUID.randomUUID().toString());

        Mockito.when(serviceManagementService.getServices()).thenReturn(Arrays.asList(serviceOne, serviceTwo));

        var response = serviceManagementController.v1ServicesGet();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        var firstBodyElement = body.get(0);
        assertEquals(serviceOne.name(), firstBodyElement.getName());
        assertEquals(serviceOne.serviceIdentifier(), firstBodyElement.getServiceIdentifier());
        assertEquals(serviceOne.ignoreServiceName(), firstBodyElement.getIgnoreServiceName());
        assertEquals(serviceOne.group(), firstBodyElement.getGroup());
        assertEquals(serviceOne.description(), firstBodyElement.getDescription());

        var secondBodyElement = body.get(1);
        assertEquals(serviceTwo.name(), secondBodyElement.getName());
        assertEquals(serviceTwo.serviceIdentifier(), secondBodyElement.getServiceIdentifier());
        assertEquals(serviceTwo.ignoreServiceName(), secondBodyElement.getIgnoreServiceName());
        assertNull(secondBodyElement.getGroup());
        assertEquals(serviceTwo.description(), secondBodyElement.getDescription());

        Mockito.verify(serviceManagementService, times(1)).getServices();
    }

    @Test
    public void testGetAllServicesNoServices() {
        Mockito.when(serviceManagementService.getServices()).thenReturn(Collections.emptyList());

        var response = serviceManagementController.v1ServicesGet();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.size());

        Mockito.verify(serviceManagementService, times(1)).getServices();
    }

    @Test
    public void testGetService() {
        var serviceUuid = UUID.randomUUID();
        var service = new ServiceModel(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID().toString());

        Mockito.when(serviceManagementService.getService(serviceUuid)).thenReturn(Optional.of(service));

        var response = serviceManagementController.v1ServicesUuidGet(serviceUuid);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);

        assertEquals(service.name(), body.getName());
        assertEquals(service.serviceIdentifier(), body.getServiceIdentifier());
        assertEquals(service.ignoreServiceName(), body.getIgnoreServiceName());
        assertEquals(service.group(), body.getGroup());
        assertEquals(service.description(), body.getDescription());

        Mockito.verify(serviceManagementService, times(1)).getService(serviceUuid);
    }

    @Test
    public void testGetServiceNotFound() {
        var serviceUuid = UUID.randomUUID();

        Mockito.when(serviceManagementService.getService(serviceUuid)).thenReturn(Optional.empty());

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> serviceManagementController.v1ServicesUuidGet(serviceUuid));
        assertNotNull(expectedException);
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());
        assertEquals("Service with uuid %s not found".formatted(serviceUuid), expectedException.getMessage());

        Mockito.verify(serviceManagementService, times(1)).getService(serviceUuid);
    }

    @Test
    public void testUpdateServiceNotFound() {
        var serviceUuid = UUID.randomUUID();
        var serviceUpdate = new ServiceUpdate();
        serviceUpdate.setServiceIdentifier(UUID.randomUUID().toString());
        serviceUpdate.setName(UUID.randomUUID().toString());
        serviceUpdate.setIgnoreServiceName(true);
        serviceUpdate.setGroup(UUID.randomUUID());
        serviceUpdate.setDescription(UUID.randomUUID().toString());

        Mockito.when(serviceManagementService.updateService(Mockito.eq(serviceUuid), Mockito.any())).thenReturn(false);

        var expectedException = assertThrows(ResourceNotFoundException.class, () ->serviceManagementController.v1ServicesUuidPut(serviceUuid, serviceUpdate));
        assertNotNull(expectedException);
        assertEquals(HttpStatus.NOT_FOUND, expectedException.getHttpStatus());
        assertEquals("Service with uuid %s not found".formatted(serviceUuid), expectedException.getMessage());

        Mockito.verify(serviceManagementService, times(1)).updateService(Mockito.eq(serviceUuid), Mockito.argThat(x -> {
            assertEquals(serviceUpdate.getServiceIdentifier(), x.serviceIdentifier());
            assertEquals(serviceUpdate.getIgnoreServiceName(), x.ignoreServiceName());
            assertEquals(serviceUpdate.getName(), x.name());
            assertEquals(serviceUpdate.getGroup(), x.group());
            assertEquals(serviceUpdate.getDescription(), x.description());

            return true;
        }));
    }

    @Test
    public void testUpdateService() {
        var serviceUuid = UUID.randomUUID();
        var serviceUpdate = new ServiceUpdate();
        serviceUpdate.setServiceIdentifier(UUID.randomUUID().toString());
        serviceUpdate.setName(UUID.randomUUID().toString());
        serviceUpdate.setIgnoreServiceName(true);
        serviceUpdate.setGroup(UUID.randomUUID());
        serviceUpdate.setDescription(UUID.randomUUID().toString());

        Mockito.when(serviceManagementService.updateService(Mockito.eq(serviceUuid), Mockito.any())).thenReturn(true);

        var response = serviceManagementController.v1ServicesUuidPut(serviceUuid, serviceUpdate);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Mockito.verify(serviceManagementService, times(1)).updateService(Mockito.eq(serviceUuid), Mockito.argThat(x -> {
            assertEquals(serviceUpdate.getServiceIdentifier(), x.serviceIdentifier());
            assertEquals(serviceUpdate.getIgnoreServiceName(), x.ignoreServiceName());
            assertEquals(serviceUpdate.getName(), x.name());
            assertEquals(serviceUpdate.getGroup(), x.group());
            assertEquals(serviceUpdate.getDescription(), x.description());

            return true;
        }));
    }

    @Test
    public void testCreateService() {
        var serviceCreate = new ServiceCreate();
        serviceCreate.setServiceIdentifier(UUID.randomUUID().toString());
        serviceCreate.setName(UUID.randomUUID().toString());
        serviceCreate.setIgnoreServiceName(true);
        serviceCreate.setGroup(UUID.randomUUID());
        serviceCreate.setDescription(UUID.randomUUID().toString());

        var expectedUuid = UUID.randomUUID();

        Mockito.when(serviceManagementService.createService(Mockito.any())).thenReturn(expectedUuid);

        var response = serviceManagementController.v1ServicesPost(serviceCreate);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUuid.toString(), response.getHeaders().get("Location").stream().findFirst().get());
        assertEquals(expectedUuid, response.getBody().getUuid());

        Mockito.verify(serviceManagementService, times(1)).createService(Mockito.argThat(x -> {
            assertEquals(serviceCreate.getServiceIdentifier(), x.serviceIdentifier());
            assertEquals(serviceCreate.getIgnoreServiceName(), x.ignoreServiceName());
            assertEquals(serviceCreate.getName(), x.name());
            assertEquals(serviceCreate.getGroup(), x.group());
            assertEquals(serviceCreate.getDescription(), x.description());

            return true;
        }));
    }

    @Test
    public void testDelete() {
        var uuid = UUID.randomUUID();

        Mockito.when(serviceManagementService.deleteService(uuid)).thenReturn(true);

        var result = serviceManagementController.v1ServicesUuidDelete(uuid);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        Mockito.verify(serviceManagementService, times(1)).deleteService(uuid);
    }

    @Test
    public void testDeleteNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(serviceManagementService.deleteService(uuid)).thenReturn(false);

        var expectedException = assertThrows(ResourceNotFoundException.class, () -> serviceManagementController.v1ServicesUuidDelete(uuid));
        assertNotNull(expectedException);

        Mockito.verify(serviceManagementService, times(1)).deleteService(uuid);
    }
}
