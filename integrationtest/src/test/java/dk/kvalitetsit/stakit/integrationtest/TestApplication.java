package dk.kvalitetsit.stakit.integrationtest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class TestApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        new ServiceStarter().startServices();
    }
}
