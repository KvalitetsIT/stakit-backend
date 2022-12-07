package dk.kvalitetsit.stakit.session.exception;

import io.jsonwebtoken.JwtException;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(JwtException e) {
        super(e);
    }
}
