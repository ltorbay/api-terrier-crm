package fr.terrier.apiterriercrm.model.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ResponseException {

    public BadRequestException(String reason, Object... args) {
        super(HttpStatus.BAD_REQUEST, reason, args);
    }

    public BadRequestException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }
}
