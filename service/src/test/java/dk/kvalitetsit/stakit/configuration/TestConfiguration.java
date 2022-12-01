package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.dao.ServiceStatusDao;
import dk.kvalitetsit.stakit.dao.ServiceStatusDaoImpl;
import dk.kvalitetsit.stakit.dao.TestDataHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
public class TestConfiguration {
    // Configure beans used for test

//    @Bean
//    public ServiceStatusDao helloDao(DataSource dataSource) {
//        return new ServiceStatusDaoImpl(dataSource);
//    }

    @Bean
    public TestDataHelper testDataHelper() {
        return new TestDataHelper();
    }
}
