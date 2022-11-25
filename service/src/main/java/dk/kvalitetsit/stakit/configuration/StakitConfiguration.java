package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.service.HelloService;
import dk.kvalitetsit.stakit.service.HelloServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StakitConfiguration {
    @Bean
    public HelloService helloService() {
        return new HelloServiceImpl();
    }
}
