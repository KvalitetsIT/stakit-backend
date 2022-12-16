package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.controller.exception.ResourceNotFoundException;
import dk.kvalitetsit.stakit.controller.mapper.ServiceManagementMapper;
import dk.kvalitetsit.stakit.service.ServiceManagementService;
import org.openapitools.api.ServiceManagementApi;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost") // TODO Jeg skal nok fjernes igen.
public class ServiceManagementController implements ServiceManagementApi {
    private final ServiceManagementService serviceManagementService;

    public ServiceManagementController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    @Override
    public ResponseEntity<List<Services>> v1ServicesGet() {
        var services = serviceManagementService.getServices();
        return ResponseEntity.ok(ServiceManagementMapper.mapServices(services));
    }

    @Override
    public ResponseEntity<CreateResponse> v1ServicesPost(ServiceCreate serviceCreate) {
        var serviceUuid = serviceManagementService.createService(ServiceManagementMapper.mapCreate(serviceCreate));

        return ResponseEntity.status(HttpStatus.CREATED).header("Location", serviceUuid.toString()).body(new CreateResponse().uuid(serviceUuid)); // TODO Jeg skal nok være en rigtig URL.
    }

    @Override
    public ResponseEntity<Void> v1ServicesUuidDelete(UUID uuid) {
        var deleted = serviceManagementService.deleteService(uuid);

        if(!deleted) {
            throw new ResourceNotFoundException("Service with uuid %s not found".formatted(uuid));
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Service> v1ServicesUuidGet(UUID uuid) {
        var service  = serviceManagementService.getService(uuid);

        return ResponseEntity.ok(service.map(ServiceManagementMapper::mapService)
                .orElseThrow(() -> new ResourceNotFoundException("Service with uuid %s not found".formatted(uuid))));
    }

    @Override
    public ResponseEntity<Void> v1ServicesUuidPut(UUID uuid, ServiceUpdate serviceUpdate) {
        boolean updated = serviceManagementService.updateService(uuid, ServiceManagementMapper.mapUpdate(serviceUpdate));

        if(!updated) {
            throw new ResourceNotFoundException("Service with uuid %s not found".formatted(uuid));
        }

        return ResponseEntity.status(201).build();
    }
}
