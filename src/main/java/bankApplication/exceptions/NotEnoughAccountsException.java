package bankApplication.exceptions;

public class NotEnoughAccountsException extends RuntimeException {
    public NotEnoughAccountsException(String message) {
        super(message);
    }
}
