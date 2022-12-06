package dk.kvalitetsit.stakit.session;

import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import dk.kvalitetsit.stakit.session.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class UserContextServiceImpl implements UserContextService {
    private static Logger logger = LoggerFactory.getLogger(UserContextServiceImpl.class);
    private final HttpServletRequest request;
    private final Optional<Token> authorizationToken;

    public UserContextServiceImpl(HttpServletRequest request) {
        this.request = request;
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
                var sessionParser = new JwtTokenParser();
                return Optional.of(sessionParser.parse(header.substring(7)));
            } catch (InvalidTokenException e) {
                logger.warn("Error parsing token", e);
            }
        }

        logger.debug("No session header found.");
        return Optional.empty();
    }

}
