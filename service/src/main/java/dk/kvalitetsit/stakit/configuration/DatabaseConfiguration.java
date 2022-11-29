package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.StatusConfigurationDao;
import dk.kvalitetsit.stakit.dao.StatusConfigurationDaoImpl;
import dk.kvalitetsit.stakit.dao.StatusDao;
import dk.kvalitetsit.stakit.dao.StatusDaoImpl;
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
    public StatusDao statusDao(DataSource dataSource) {
        return new StatusDaoImpl(dataSource);
    }

    @Bean
    public StatusConfigurationDao statusConfigurationDao(DataSource dataSource) {
        return new StatusConfigurationDaoImpl(dataSource);
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