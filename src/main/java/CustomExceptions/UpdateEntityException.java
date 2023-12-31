package CustomExceptions;

public class UpdateEntityException extends RuntimeException {
    public UpdateEntityException() {
        super();
    }

    public UpdateEntityException(String message) {
        super(message);
    }

    public UpdateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
