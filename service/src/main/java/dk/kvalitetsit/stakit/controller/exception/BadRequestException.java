package dk.kvalitetsit.stakit.controller.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractApiException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
