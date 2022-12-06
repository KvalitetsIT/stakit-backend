package dk.kvalitetsit.stakit.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundExceptionAbstract extends AbstractApiException {
    public ResourceNotFoundExceptionAbstract(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}
