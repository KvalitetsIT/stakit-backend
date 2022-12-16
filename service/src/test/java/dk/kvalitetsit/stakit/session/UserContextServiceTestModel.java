package dk.kvalitetsit.stakit.session;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserContextServiceTestModel {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private HttpServletRequest request;
    private String username;

    @Before
    public void setup() {
        request = Mockito.mock(HttpServletRequest.class);

        username = "some_username";
    }

    @Test
    public void testInvalidHeaderReturnFalse() {
        Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer ÆÅØ");
        UserContextServiceImpl userContextService = new UserContextServiceImpl(request);

        assertFalse(userContextService.hasValidAuthorizationToken());
    }

    @Test
    public void testNoHeaderReturnFalse() {
        Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);
        UserContextServiceImpl userContextService = new UserContextServiceImpl(request);

        assertFalse(userContextService.hasValidAuthorizationToken());
    }

    @Test
    public void testValidHeaderReturnFalse() throws Exception {
        Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer " + generateSignedToken());
        UserContextServiceImpl userContextService = new UserContextServiceImpl(request);

        assertTrue(userContextService.hasValidAuthorizationToken());
    }

    private String generateSignedToken(String... roles) throws Exception {
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
                .claim("preferred_username", username)
                .claim("roles", roles)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
