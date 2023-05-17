package dk.kvalitetsit.stakit.integrationtest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static dk.kvalitetsit.stakit.integrationtest.AbstractIntegrationTest.generateSignedToken;

@SpringBootApplication
public class TestApplication extends SpringBootServletInitializer {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        System.out.printf("Token %s\n", TokenGenerator.generateSignedToken());

        new ServiceStarter().startServices();
    }
}
