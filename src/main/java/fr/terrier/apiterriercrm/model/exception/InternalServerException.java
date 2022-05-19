package fr.terrier.apiterriercrm.model.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(String message, Object... args) {
        super(String.format(message, args));
    }
}
