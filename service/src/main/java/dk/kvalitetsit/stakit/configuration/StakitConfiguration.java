package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StakitConfiguration {
    @Bean
    public StatusUpdateService statusUpdateService(ServiceConfigurationDao serviceConfigurationDao, ServiceStatusDao serviceStatusDao) {
        return new StatusUpdateServiceImpl(serviceConfigurationDao, serviceStatusDao);
    }

    @Bean
    public StatusGroupService statusGroupService(GroupedStatusDao groupedStatusDao) {
        return new StatusGroupServiceImpl(groupedStatusDao);
    }

    @Bean
    public GroupService groupService(GroupConfigurationDao groupConfigurationDao) {
        return new GroupServiceImpl(groupConfigurationDao);
    }

    @Bean
    public ServiceManagementService serviceManagementService(ServiceConfigurationDao serviceConfigurationDao, GroupConfigurationDao groupConfigurationDao) {
        return new ServiceManagementServiceImpl(serviceConfigurationDao, groupConfigurationDao);
    }
}
