package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceModel;
import dk.kvalitetsit.stakit.session.ApiKey;
import org.openapitools.api.AdapterApi;
import org.openapitools.model.StatusUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdapterController implements AdapterApi {
    private static final Logger logger = LoggerFactory.getLogger(AdapterController.class);
    private final StatusUpdateService statusUpdateService;

    public AdapterController(StatusUpdateService statusUpdateService) {
        this.statusUpdateService = statusUpdateService;
    }
    
    @Override
    @ApiKey
    public ResponseEntity<Void> v1StatusPost(StatusUpdate statusUpdate) {
        logger.debug("Updating or creating status");

        var upd = new UpdateServiceModel(
                statusUpdate.getService(),
                statusUpdate.getServiceName(),
                Status.valueOf(Status.class, statusUpdate.getStatus().toString()),
                statusUpdate.getStatusTime(),
                statusUpdate.getMessage());

        statusUpdateService.updateStatus(upd);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
