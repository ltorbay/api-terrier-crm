package fr.terrier.apiterriercrm.model.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseException extends ResponseStatusException {
    public ResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ResponseException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
