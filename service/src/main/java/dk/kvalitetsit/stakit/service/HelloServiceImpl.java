package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.HelloServiceInput;
import dk.kvalitetsit.stakit.service.model.HelloServiceOutput;

import java.time.ZonedDateTime;

public class HelloServiceImpl implements HelloService {
    @Override
    public HelloServiceOutput helloServiceBusinessLogic(HelloServiceInput input) {
        return new HelloServiceOutput(input.name(), ZonedDateTime.now());
    }
}
