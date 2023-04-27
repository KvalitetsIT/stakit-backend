package dk.kvalitetsit.stakit.integrationtest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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

public class TokenGenerator {
    static String generateSignedToken() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        return TokenGenerator.generateSignedToken();
    }

}
