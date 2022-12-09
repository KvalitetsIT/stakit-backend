package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration {
    @Bean
    public ServiceStatusDao statusDao(DataSource dataSource) {
        return new ServiceStatusDaoImpl(dataSource);
    }

    @Bean
    public ServiceConfigurationDao statusConfigurationDao(DataSource dataSource) {
        return new ServiceConfigurationDaoImpl(dataSource);
    }

    @Bean
    public GroupConfigurationDao groupDao(DataSource dataSource) {
        return new GroupConfigurationDaoImpl(dataSource);
    }

    @Bean
    public GroupedStatusDao groupedStatusDao(DataSource dataSource) {
        return new GroupedStatusDaoImpl(dataSource);
    }

    @Bean
    public AnnouncementDao announcementDao(DataSource dataSource) {
        return new AnnouncementDaoImpl(dataSource);
    }

    @Bean
    public MailSubscriptionDao mailSubscriptionDao(DataSource dataSource) {
        return new MailSubscriptionDaoImpl(dataSource);
    }

    @Bean
    public MailSubscriptionGroupDao mailSubscriptionGroupsEntity(DataSource dataSource) {
        return new MailSubscriptionGroupDaoImpl(dataSource);
    }

    @Bean
    public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl, @Value("${jdbc.user}") String jdbcUser, @Value("${jdbc.pass}") String jdbcPass) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUser);
        dataSource.setPassword(jdbcPass);

        return dataSource;
    }
}