package dk.kvalitetsit.stakit.session;

import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;

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
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class JwtTokenParserTest {
    private JwtTokenParser tokenParser;

    @Before
    public void setup() {
        tokenParser = new JwtTokenParser();
    }

    @Test
    public void testValidToken() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException {
        var token = generateSignedToken();

        var parser = new JwtTokenParser();

        var result = parser.parse(token);
        assertNotNull(result);
    }

    @Test(expected = InvalidTokenException.class)
    public void testInvalidToken() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException {
        var token = generateSignedToken();

        var parser = new JwtTokenParser();

        parser.parse( "invalid" + token);
    }

    private String generateSignedToken() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
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
                .claim("preferred_username", "some_username")
                .claim("org", "some org")
                .claim("sub", "f90f3684-6f0d-420b-a794-85ed2588a4cd")
                .claim("roles", Arrays.asList("r1", "r2"))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
