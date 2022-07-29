package fr.terrier.apiterriercrm.model.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseException extends ResponseStatusException {
    // TODO add internal error codes so they can be mapped to user understandable messages
    // TODO add serialize method to be implemented by child classes (update this to abstract ?)
    public ResponseException(HttpStatus status, String reason, Object... args) {
        super(status, String.format(reason, args));
    }

    public ResponseException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
