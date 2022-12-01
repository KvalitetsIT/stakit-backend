package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.service.*;
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

    @Bean
    public GroupService groupService(GroupConfigurationDao groupConfigurationDao) {
        return new GroupServiceImpl(groupConfigurationDao);
    }
}
