package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.StatusUpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StakitConfiguration {
    @Bean
    public StatusUpdateService helloService() {
        return new StatusUpdateServiceImpl();
    }
}
