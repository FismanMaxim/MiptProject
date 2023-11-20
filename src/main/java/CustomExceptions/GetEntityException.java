package CustomExceptions;

public class GetEntityException extends RuntimeException {
    public GetEntityException() {
    }

    public GetEntityException(String message) {
        super(message);
    }

    public GetEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
