package dk.kvalitetsit.stakit.session;


import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import dk.kvalitetsit.stakit.session.model.Token;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtTokenParser {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenParser.class);

    public Token parse(String token) {
        logger.debug("Parsing token: {}", token);

        try {
            Jwt<Header, Claims> untrustedToken = parseUntrusted(token);

            var tokenResult = new Token();
            // Set stuff on token if needed.

            return tokenResult;
        }
        catch (JwtException e) {
            logger.warn("Error parsing JWT header.", e);
            throw new InvalidTokenException(e);
        }
    }

    private Jwt<Header, Claims> parseUntrusted(String token) {
        int i = token.lastIndexOf('.');
        String tokenWithoutSignature = token.substring(0, i+1);

        return Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(tokenWithoutSignature);
    }
}
