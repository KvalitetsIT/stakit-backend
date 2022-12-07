package dk.kvalitetsit.stakit.controller.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AbstractApiException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
