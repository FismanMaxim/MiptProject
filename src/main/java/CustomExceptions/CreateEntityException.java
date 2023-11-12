package CustomExceptions;

public class CreateEntityException extends Exception {
    public CreateEntityException() {
        super();
    }

    public CreateEntityException(String message) {
        super(message);
    }

    public CreateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
