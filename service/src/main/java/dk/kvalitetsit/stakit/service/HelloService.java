package dk.kvalitetsit.stakit.service;

import dk.kvalitetsit.stakit.service.model.HelloServiceOutput;
import dk.kvalitetsit.stakit.service.model.HelloServiceInput;

public interface HelloService {
    HelloServiceOutput helloServiceBusinessLogic(HelloServiceInput input);
}
