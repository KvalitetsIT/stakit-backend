package dk.kvalitetsit.stakit.controller;

import org.openapitools.api.ServiceManagementApi;
import org.openapitools.model.Service;
import org.openapitools.model.ServiceCreate;
import org.openapitools.model.ServiceUpdate;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

public class ServiceController implements ServiceManagementApi {
    @Override
    public ResponseEntity<List<Service>> v1ServicesGet() {
        return null;
    }

    @Override
    public ResponseEntity<Void> v1ServicesPost(ServiceCreate serviceCreate) {
        return null;
    }

    @Override
    public ResponseEntity<Service> v1ServicesUuidGet(UUID uuid) {
        return null;
    }

    @Override
    public ResponseEntity<Void> v1ServicesUuidPut(UUID uuid, ServiceUpdate serviceUpdate) {
        return null;
    }
}
