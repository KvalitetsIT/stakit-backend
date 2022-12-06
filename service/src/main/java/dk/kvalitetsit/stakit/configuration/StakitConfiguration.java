package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.GroupConfigurationDao;
import dk.kvalitetsit.stakit.dao.GroupedStatusDao;
import dk.kvalitetsit.stakit.dao.ServiceConfigurationDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.service.*;
import dk.kvalitetsit.stakit.session.ApiAccessInterceptor;
import dk.kvalitetsit.stakit.session.UserContextService;
import dk.kvalitetsit.stakit.session.UserContextServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class StakitConfiguration implements WebMvcConfigurer {
    private ApiAccessInterceptor apiAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAccessInterceptor);
    }

    @Bean
    public ApiAccessInterceptor apiAccessInterceptor(UserContextService userContextService) {
        apiAccessInterceptor = new ApiAccessInterceptor(userContextService);

        return apiAccessInterceptor;
    }

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

    @Bean
    @RequestScope
    public UserContextService userContextService(HttpServletRequest request) {
        return new UserContextServiceImpl(request);
    }
}
