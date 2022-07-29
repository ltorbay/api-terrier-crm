package fr.terrier.apiterriercrm.model.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ResponseException {
    public InternalServerException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

    public InternalServerException(String message, Object... args) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format(message, args));
    }
}
