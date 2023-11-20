package CustomExceptions;

public class NegativeMoneyException extends RuntimeException {
    public NegativeMoneyException() {
        super();
    }

    public NegativeMoneyException(String message) {
        super(message);
    }

    public NegativeMoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}
