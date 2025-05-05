package dk.kvalitetsit.stakit.integrationtest;

import dk.kvalitetsit.stakit.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ServiceStarter {
    public static final String API_KEY = "API_KEY";
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("stakit-backend");
    private static final Logger mariadbLogger = LoggerFactory.getLogger("mariadb");
    private static final Logger mockSmtpLogger = LoggerFactory.getLogger("mock-smtp");

    private static Network dockerNetwork;
    private static String jdbcUrl;
    public static final String DB_USER = "hellouser";
    public static final String DB_PASSWORD = "secret1234";
    private static String smtpHost;
    private static int smtpWebPort;
    private static int smtpPort;

    private static boolean firstStart = true;

    public final String jwtSigningKey = """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2mtNppdwimPy/XBbv672
            34QXh3dX5VCKKJIFZU2m1o54PgQioNLh5ETDkDoMtbii7BGlEn6DpEtt656nuMMw
            WsZRlmU2nXmwoXvSkbIXwchZRgGQSjdPhJxIrAFs0iAD4Qy35vh/D76sGi+2M0Ek
            R1vNDYaBNjt6r2nODsHlnejU25FPxcB6nA4No2RfB9B8Fqc0Gmj01G2FKiKvezbt
            Ag25b1R/NIn9nfzcF0DbMAqYl4nFZC7KJw2XWjo2/ybLdll+mMPJZKiN8HrND7PR
            QceW/9S28ZtELFPMWIbHrvvRYb6fMLwxDZuL7zeoZ2Hgy+HHWCC0lmrEfPe9l38G
            XQIDAQAB
            -----END PUBLIC KEY-----
            """;

    public void startServices() {
        firstStart = false;
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();
        setupMockSmtp();

        System.setProperty("JDBC.URL", jdbcUrl);
        System.setProperty("JDBC.USER", DB_USER);
        System.setProperty("JDBC.PASS", DB_PASSWORD);

        System.setProperty("MAIL_HOST", "localhost");
        System.setProperty("MAIL_PORT", "" + smtpPort);
        System.setProperty("MAIL_USER", "some_user");
        System.setProperty("MAIL_PASSWORD", "some_password");
        System.setProperty("MAIL_FROM", "from_email");
        System.setProperty("STATUS_UPDATE_SUBJECT_TEMPLATE", "Subject");
        System.setProperty("STATUS_UPDATE_BODY_TEMPLATE", "Mail body.");
        System.setProperty("ADAPTER_API_KEY", API_KEY);
        System.setProperty("JWT_SIGNING_KEY", jwtSigningKey );
        System.setProperty("ALLOWED_ORIGINS", "*");
        System.setProperty("BASE_URL", "http://baseUrl:8080");
        System.setProperty("CHECK_MESSAGES_FREQUENCY", "PT5S");

        SpringApplication.run((Application.class));
    }

    private void setupMockSmtp() {
        GenericContainer<?> mockSmtp = new GenericContainer<>("mailhog/mailhog")
                .withNetwork(dockerNetwork)
                .withExposedPorts(1025, 8025)
                .withNetworkAliases("smtp")
                .waitingFor(Wait.forHttp("/").forPort(8025));
        mockSmtp.start();

        attachLogger(mockSmtpLogger, mockSmtp);

        smtpHost = mockSmtp.getHost();
        smtpWebPort = mockSmtp.getMappedPort(8025);
        smtpPort = mockSmtp.getMappedPort(1025);
    }

    String getSmtpHost() {
        return smtpHost;
    }

    int getSmtpWebPort() {
        return smtpWebPort;
    }

    public GenericContainer<?> startServicesInDocker() {
        if (firstStart) {
            firstStart = false;

            dockerNetwork = Network.newNetwork();

            setupDatabaseContainer();
            setupMockSmtp();
        }

        GenericContainer<?> service;

        // Start service
        service = new GenericContainer<>("local/stakit-backend-qa:dev")
                .withFileSystemBind("/tmp", "/jacoco-output/", BindMode.READ_WRITE)
                .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-output/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");

        service.withNetwork(dockerNetwork)
                .withNetworkAliases("stakit-backend")

                .withEnv("LOG_LEVEL", "INFO")

                .withEnv("JDBC_URL", "jdbc:mariadb://mariadb:3306/hellodb")
                .withEnv("JDBC_USER", DB_USER)
                .withEnv("JDBC_PASS", DB_PASSWORD)

                .withEnv("MAIL_HOST", "smtp")
                .withEnv("MAIL_PORT", "" + 1025)
                .withEnv("MAIL_USER", "mail_user")
                .withEnv("MAIL_PASSWORD", "mail_password")
                .withEnv("MAIL_FROM", "from_email")
                .withEnv("STATUS_UPDATE_SUBJECT_TEMPLATE", "Subject")
                .withEnv("STATUS_UPDATE_BODY_TEMPLATE", "Mail body.")
                .withEnv("ADAPTER_API_KEY", API_KEY)
                .withEnv("ALLOWED_ORIGINS", "*")
                .withEnv("JWT_SIGNING_KEY", jwtSigningKey )
                .withEnv("BASE_URL", "http://base_url:8080")
                .withEnv("CHECK_MESSAGES_FREQUENCY", "PT5S")

                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
                .withClasspathResourceMapping("db/migration/V901__extra_data_for_integration_test.sql", "/app/sql/V901__extra_data_for_integration_test.sql", BindMode.READ_ONLY)
//                .withEnv("JVM_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000")

                .withExposedPorts(8081,8080)
                .waitingFor(Wait.forHttp("/actuator").forPort(8081).forStatusCode(200));
        service.start();
        attachLogger(serviceLogger, service);

        return service;
    }

    private void setupDatabaseContainer() {
        // Database server.
        var mariadb = new MariaDBContainer<>("mariadb:10.6")
                .withDatabaseName("hellodb")
                .withUsername("hellouser")
                .withPassword("secret1234")
                .withNetwork(dockerNetwork)
                .withNetworkAliases("mariadb");
        mariadb.start();
        jdbcUrl = mariadb.getJdbcUrl();
        attachLogger(mariadbLogger, mariadb);
    }

    private void attachLogger(Logger logger, GenericContainer<?> container) {
        ServiceStarter.logger.info("Attaching logger to container: {}", container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public boolean isFirstStart() {
        return firstStart;
    }
}
