package dk.kvalitetsit.stakit.controller;

import dk.kvalitetsit.stakit.service.HelloService;
import dk.kvalitetsit.stakit.service.model.HelloServiceInput;
import dk.kvalitetsit.stakit.service.model.HelloServiceOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openapitools.model.HelloRequest;

import java.time.ZonedDateTime;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

public class StakitControllerTest {
    private StakitController stakitController;
    private HelloService helloService;

    @Before
    public void setup() {
        helloService = Mockito.mock(HelloService.class);

        stakitController = new StakitController(helloService);
    }

    @Test
    public void testCallController() {
        var input = new HelloRequest();
        input.setName(UUID.randomUUID().toString());

        var expectedDate = ZonedDateTime.now();
        Mockito.when(helloService.helloServiceBusinessLogic(Mockito.any())).then(a -> new HelloServiceOutput(a.getArgument(0, HelloServiceInput.class).name(), expectedDate));

        var result = stakitController.v1HelloPost(input);

        assertNotNull(result);
        assertEquals(input.getName(), result.getBody().getName());
        assertEquals(expectedDate.toOffsetDateTime(), result.getBody().getNow());

        var inputArgumentCaptor = ArgumentCaptor.forClass(HelloServiceInput.class);
        Mockito.verify(helloService, times(1)).helloServiceBusinessLogic(inputArgumentCaptor.capture());

        assertNotNull(inputArgumentCaptor.getValue());
        assertEquals(input.getName(), inputArgumentCaptor.getValue().name());
    }
}
