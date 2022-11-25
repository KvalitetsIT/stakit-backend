package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.HelloService;
import dk.kvalitetsit.stakit.service.model.HelloServiceInput;
import org.openapitools.api.StaKitApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StakitController implements StaKitApi {
    private static final Logger logger = LoggerFactory.getLogger(StakitController.class);
    private final HelloService helloService;

    public StakitController(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public ResponseEntity<org.openapitools.model.HelloResponse> v1HelloPost(org.openapitools.model.HelloRequest helloRequest) {
        logger.debug("Enter POST stakit.");

        var serviceInput = new HelloServiceInput(helloRequest.getName());

        var serviceResponse = helloService.helloServiceBusinessLogic(serviceInput);

        var helloResponse = new org.openapitools.model.HelloResponse();
        helloResponse.setName(serviceResponse.name());
        helloResponse.setNow(serviceResponse.now().toOffsetDateTime());

        return ResponseEntity.ok(helloResponse);
    }
}
