package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.service.StatusGroupService;
import dk.kvalitetsit.stakit.service.StatusGroupServiceImpl;
import dk.kvalitetsit.stakit.service.StatusUpdateService;
import dk.kvalitetsit.stakit.service.StatusUpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StakitConfiguration {
    @Bean
    public StatusUpdateService statusUpdateService(StatusConfigurationDao statusConfigurationDao, StatusDao statusDao) {
        return new StatusUpdateServiceImpl(statusConfigurationDao, statusDao);
    }

    @Bean
    public StatusGroupService statusGroupService(GroupedStatusDao groupedStatusDao) {
        return new StatusGroupServiceImpl(groupedStatusDao);
    }
}
