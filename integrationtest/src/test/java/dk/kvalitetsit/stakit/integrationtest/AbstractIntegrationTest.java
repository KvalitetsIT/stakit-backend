package dk.kvalitetsit.stakit.integrationtest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static GenericContainer helloService;
    private static String apiBasePath;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                if(helloService != null) {
                    logger.info("Stopping stakit service container: " + helloService.getContainerId());
                    helloService.getDockerClient().stopContainerCmd(helloService.getContainerId()).exec();
                }
            }
        });

        try {
            setup();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setup() throws IOException, URISyntaxException {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: " + runInDocker);

        ServiceStarter serviceStarter;
        serviceStarter = new ServiceStarter();
        if(runInDocker) {
            helloService = serviceStarter.startServicesInDocker();
            apiBasePath = "http://" + helloService.getContainerIpAddress() + ":" + helloService.getMappedPort(8080);
        }
        else {
            serviceStarter.startServices();
            apiBasePath = "http://localhost:8080";
        }
    }

    String getApiBasePath() {
        return apiBasePath;
    }

    String generateSignedToken() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var privateKeyBytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("key.pkcs8").toURI()));

        var spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        var privateKey = kf.generatePrivate(spec);

        return Jwts.builder()
                .setNotBefore(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(1L).toInstant(ZoneOffset.UTC)))
                .setAudience("audience")
                .setIssuer("issuer")
                .claim("email", "john@example.com")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
