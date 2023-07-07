package dk.kvalitetsit.stakit.session;

import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import dk.kvalitetsit.stakit.session.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserContextServiceImpl implements UserContextService {
    private static final Logger logger = LoggerFactory.getLogger(UserContextServiceImpl.class);
    private final HttpServletRequest request;
    private final JwtTokenParser tokenParser;
    private final Optional<Token> authorizationToken;

    public UserContextServiceImpl(HttpServletRequest request, JwtTokenParser tokenParser) {
        this.request = request;
        this.tokenParser = tokenParser;
        this.authorizationToken = readOptionalAuthorizationToken();
        logger.debug("Creating new instance.");
    }

    @Override
    public boolean hasValidAuthorizationToken() {
        return authorizationToken.isPresent();
    }

    private Optional<Token> readOptionalAuthorizationToken() {
        String header = request.getHeader("Authorization");

        if (header != null) {
            logger.debug("Session header found. Value: {}", header);
            try {
                return Optional.of(tokenParser.parse(header.substring(7)));
            } catch (InvalidTokenException e) {
                logger.warn("Error parsing token", e);
            }
        }

        logger.debug("No session header found.");
        return Optional.empty();
    }

}
