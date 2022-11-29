package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.StatusUpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StakitConfiguration {
    @Bean
    public StatusUpdateService helloService(StatusConfigurationDao statusConfigurationDao, StatusDao statusDao) {
        return new StatusUpdateServiceImpl(statusConfigurationDao, statusDao);
    }
}
