package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.model.Status;
import dk.kvalitetsit.stakit.service.model.UpdateServiceInput;
import org.openapitools.api.StaKitApi;
import org.openapitools.model.StatusUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StakitController implements StaKitApi {
    private static final Logger logger = LoggerFactory.getLogger(StakitController.class);
    private final StatusUpdateService statusUpdateService;

    public StakitController(StatusUpdateService statusUpdateService) {
        this.statusUpdateService = statusUpdateService;
    }

    @Override
    public ResponseEntity<Void> v1StatusPost(StatusUpdate statusUpdate) {
        logger.debug("Updating or creating status");

        statusUpdateService.updateStatus(new UpdateServiceInput(statusUpdate.getService(), Status.valueOf(Status.class, statusUpdate.getStatus().toString()), statusUpdate.getStatusTime(), statusUpdate.getMessage()));

        return ResponseEntity.ok().build();
    }
}
