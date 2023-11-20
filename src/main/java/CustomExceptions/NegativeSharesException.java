package CustomExceptions;

public class NegativeSharesException extends RuntimeException {
    public NegativeSharesException() {
        super();
    }

    public NegativeSharesException(String message) {
        super(message);
    }

    public NegativeSharesException(String message, Throwable cause) {
        super(message, cause);
    }
}
