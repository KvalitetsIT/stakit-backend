package dk.kvalitetsit.stakit.controller.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractApiException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
