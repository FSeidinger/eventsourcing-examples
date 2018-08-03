package de.seidinger.frank.banking.application.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CouldNotReadError extends RuntimeException {
    public CouldNotReadError(String message) {
        super(message);
    }

    public CouldNotReadError(String message, Throwable cause) {
        super(message, cause);
    }
}
