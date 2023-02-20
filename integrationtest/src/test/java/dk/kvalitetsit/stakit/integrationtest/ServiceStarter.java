package dk.kvalitetsit.stakit.integrationtest;

import com.github.dockerjava.api.model.VolumesFrom;
import dk.kvalitetsit.stakit.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;

public class ServiceStarter {
    public static final String API_KEY = "API_KEY";
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("stakit-backend");
    private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");
    private static final Logger mockSmtpLogger = LoggerFactory.getLogger("mock-smtp");

    private Network dockerNetwork;
    private String jdbcUrl;
    private GenericContainer<?> mockSmtp;
    private String smtpHost;
    private int smtpWebPort;
    private int smtpPort;

    public void startServices() {
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();
        setupMockSmtp();

        System.setProperty("JDBC.URL", jdbcUrl);
        System.setProperty("JDBC.USER", "hellouser");
        System.setProperty("JDBC.PASS", "secret1234");

        System.setProperty("MAIL_HOST", "localhost");
        System.setProperty("MAIL_PORT", "" + smtpPort);
        System.setProperty("MAIL_USER", "some_user");
        System.setProperty("MAIL_PASSWORD", "some_password");
        System.setProperty("MAIL_FROM", "from_email");
        System.setProperty("STATUS_UPDATE_SUBJECT_TEMPLATE", "Subject");
        System.setProperty("STATUS_UPDATE_BODY_TEMPLATE", "Mail body.");
        System.setProperty("ADAPTER_API_KEY", API_KEY);
        System.setProperty("JWT_SIGNING_KEY", "src/test/resources/key.pub" );
        System.setProperty("ALLOWED_ORIGINS", "*");

        SpringApplication.run((Application.class));
    }

    private void setupMockSmtp() {
        mockSmtp = new GenericContainer<>("mailhog/mailhog")
                .withNetwork(dockerNetwork)
                .withExposedPorts(1025, 8025)
                .withNetworkAliases("smtp")
                .waitingFor(Wait.forHttp("/").forPort(8025));
        mockSmtp.start();

        attachLogger(mockSmtpLogger, mockSmtp);

        smtpHost = mockSmtp.getContainerIpAddress();
        smtpWebPort = mockSmtp.getMappedPort(8025);
        smtpPort = mockSmtp.getMappedPort(1025);
    }

    String getSmtpHost() {
        return smtpHost;
    }

    int getSmtpWebPort() {
        return smtpWebPort;
    }

    public GenericContainer startServicesInDocker() {
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();
        setupMockSmtp();

        var resourcesContainerName = "stakit-backend-resources";
        var resourcesRunning = containerRunning(resourcesContainerName);
        logger.info("Resource container is running: " + resourcesRunning);

        GenericContainer service;

        // Start service
        if (resourcesRunning) {
            VolumesFrom volumesFrom = new VolumesFrom(resourcesContainerName);
            service = new GenericContainer<>("local/stakit-backend-qa:dev")
                    .withCreateContainerCmdModifier(modifier -> modifier.withVolumesFrom(volumesFrom))
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true,append=true -cp integrationtest.jar");
        } else {
            service = new GenericContainer<>("local/stakit-backend-qa:dev")
                    .withFileSystemBind("/tmp", "/jacoco-report/")
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");
        }

        service.withNetwork(dockerNetwork)
                .withNetworkAliases("stakit-backend")

                .withEnv("LOG_LEVEL", "INFO")

                .withEnv("JDBC_URL", "jdbc:mariadb://mariadb:3306/hellodb")
                .withEnv("JDBC_USER", "hellouser")
                .withEnv("JDBC_PASS", "secret1234")

                .withEnv("MAIL_HOST", "smtp")
                .withEnv("MAIL_PORT", "" + 1025)
                .withEnv("MAIL_USER", "mail_user")
                .withEnv("MAIL_PASSWORD", "mail_password")
                .withEnv("MAIL_FROM", "from_email")
                .withEnv("STATUS_UPDATE_SUBJECT_TEMPLATE", "Subject")
                .withEnv("STATUS_UPDATE_BODY_TEMPLATE", "Mail body.")
                .withEnv("ADAPTER_API_KEY", API_KEY)
                .withEnv("ALLOWED_ORIGINS", "*")
                .withEnv("JWT_SIGNING_KEY", "/tmp/signing.pub" )

                .withClasspathResourceMapping("key.pub", "/tmp/signing.pub", BindMode.READ_ONLY)

                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
                .withClasspathResourceMapping("db/migration/V901__extra_data_for_integration_test.sql", "/app/sql/V901__extra_data_for_integration_test.sql", BindMode.READ_ONLY)
//                .withEnv("JVM_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000")

                .withExposedPorts(8081,8080)
                .waitingFor(Wait.forHttp("/actuator").forPort(8081).forStatusCode(200));
        service.start();
        attachLogger(serviceLogger, service);

        return service;
    }

    private boolean containerRunning(String containerName) {
        return DockerClientFactory
                .instance()
                .client()
                .listContainersCmd()
                .withNameFilter(Collections.singleton(containerName))
                .exec()
                .size() != 0;
    }

    private void setupDatabaseContainer() {
        // Database server for Organisation.
        MariaDBContainer mariadb = (MariaDBContainer) new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("hellodb")
                .withUsername("hellouser")
                .withPassword("secret1234")
                .withNetwork(dockerNetwork)
                .withNetworkAliases("mariadb");
        mariadb.start();
        jdbcUrl = mariadb.getJdbcUrl();
        attachLogger(mariadbLogger, mariadb);
    }

    private void attachLogger(Logger logger, GenericContainer container) {
        ServiceStarter.logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }
}
