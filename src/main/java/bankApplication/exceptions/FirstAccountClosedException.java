package bankApplication.exceptions;

public class FirstAccountClosedException extends RuntimeException {
    public FirstAccountClosedException(String message) {
        super(message);
    }
}
