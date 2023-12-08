package CustomExceptions;

public class DeleteEntityException extends RuntimeException {
    public DeleteEntityException() {
        super();
    }

    public DeleteEntityException(String message) {
        super(message);
    }

    public DeleteEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
