package bankApplication.exceptions;

public class SameSenderException extends RuntimeException {
    public SameSenderException(String message) {
        super(message);
    }
}
