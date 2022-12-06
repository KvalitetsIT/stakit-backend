package dk.kvalitetsit.stakit.session.exception;

import dk.kvalitetsit.stakit.controller.exception.AbstractApiException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AbstractApiException {
    public InvalidTokenException(JwtException e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }

    public InvalidTokenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
