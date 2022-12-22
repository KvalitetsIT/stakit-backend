package dk.kvalitetsit.stakit.session;

import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTokenParserTest {
    private JwtTokenParser tokenParser;

    private final String trustedKey = "key.pkcs8";
    private final String trustedPublicCertificate = "key.pub";
    private final String untrustedKey = "untrusted.pkcs8";

    @Before
    public void setup() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        var privateKeyString = Files.readString(Paths.get(ClassLoader.getSystemResource(trustedPublicCertificate).toURI()));
        tokenParser = new JwtTokenParser(privateKeyString);
    }

    @Test
    public void testValidToken() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException {
        var token = generateSignedToken(trustedKey);

        var result = tokenParser.parse(token);
        assertNotNull(result);
    }

    @Test
    public void testUntrustedToken() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException {
        var token = generateSignedToken(untrustedKey);

        var result = assertThrows(InvalidTokenException.class, () -> tokenParser.parse(token));
        assertNotNull(result);
        assertTrue(result.getCause() instanceof SignatureException);
    }

    @Test
    public void testUnsigned() throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var token = generateSignedToken(trustedKey);

        int i = token.lastIndexOf('.');
        String tokenWithoutSignature = token.substring(0, i+1);

        var result = assertThrows(InvalidTokenException.class, () -> tokenParser.parse( tokenWithoutSignature));
        assertNotNull(result);
        assertTrue(result.getCause() instanceof UnsupportedJwtException);
    }

    @Test(expected = InvalidTokenException.class)
    public void testInvalidToken() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException {
        var token = generateSignedToken(trustedKey);

        tokenParser.parse( "invalid" + token);
    }

    private String generateSignedToken(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, IOException {
        var privateKeyBytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(key).toURI()));

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
