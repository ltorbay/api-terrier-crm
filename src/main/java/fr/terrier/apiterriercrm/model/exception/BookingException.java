package fr.terrier.apiterriercrm.model.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends ResponseException {

    public BookingException(String reason, Object... args) {
        super(HttpStatus.BAD_REQUEST, reason, args);
    }

    public BookingException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }
}
