package dk.kvalitetsit.stakit.integrationtest;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static io.restassured.RestAssured.given;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static GenericContainer<?> helloService;
    private static String apiBasePath;
    private static String smtpHost;
    private static int smtpWebPort;
    private static String jdbcUrl;

    @AfterClass
    public static void afterClass() {
        if(helloService != null) {
            logger.info("Stopping stakit service container: {}", helloService.getContainerId());
            helloService.getDockerClient().stopContainerCmd(helloService.getContainerId()).exec();
        }
    }

    @BeforeClass
    public static void beforeClass() {
        setup();
    }

    private static void setup() {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: {}", runInDocker);

        ServiceStarter serviceStarter;
        serviceStarter = new ServiceStarter();
        if(runInDocker) {
            helloService = serviceStarter.startServicesInDocker();
            apiBasePath = "http://" + helloService.getHost() + ":" + helloService.getMappedPort(8080);
        }
        else if(serviceStarter.isFirstStart()){
            serviceStarter.startServices();
            apiBasePath = "http://localhost:8080";
        }

        smtpHost = serviceStarter.getSmtpHost();
        smtpWebPort = serviceStarter.getSmtpWebPort();
        jdbcUrl = serviceStarter.getJdbcUrl();
    }

    String getApiBasePath() {
        return apiBasePath;
    }

    String getSmtpHost() {
        return smtpHost;
    }

    int getSmtpWebPort() {
        return smtpWebPort;
    }

    static String generateSignedToken() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        return TokenGenerator.generateSignedToken();
    }

    int getCurrentMailCount() {
        RestAssured.baseURI = "http://" + getSmtpHost();
        RestAssured.port = getSmtpWebPort();
        RestAssured.basePath = "/api/v2";

        return given()
                .when()
                .get("/messages")
                .then()
                .extract()
                .path("total");
    }

    String getJdbcUrl() {
        return jdbcUrl;
    }
}
