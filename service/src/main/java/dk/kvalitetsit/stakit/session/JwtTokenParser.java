package dk.kvalitetsit.stakit.session;


import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import dk.kvalitetsit.stakit.session.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

public class JwtTokenParser {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenParser.class);
    private final PublicKey privateKey;

    public JwtTokenParser(String pemTrustKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Pattern parse = Pattern.compile("(?m)(?s)^---*BEGIN.*---*$(.*)^---*END.*---*$.*");
        String encoded = parse.matcher(pemTrustKey).replaceFirst("$1");

        var spec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(encoded));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        privateKey = kf.generatePublic(spec);
    }

    /**
     * Parse JWT token. Throws exception if token is not valid.
     * @param token
     * @return
     */
    public Token parse(String token) throws JwtException {
        logger.debug("Parsing token: {}", token);

        try {
            Jws<Claims> claims = parseToken(token);

            var tokenResult = new Token();
            // Set stuff on token if needed.

            return tokenResult;
        }
        catch (JwtException e) {
            logger.warn("Error parsing JWT header.", e);
            throw new InvalidTokenException(e);
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(privateKey)
                .build()
                .parseClaimsJws(token);
    }
}
