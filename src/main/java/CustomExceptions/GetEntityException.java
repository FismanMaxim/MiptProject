package CustomExceptions;

public class GetEntityException extends Exception {
    public GetEntityException() {
    }

    public GetEntityException(String message) {
        super(message);
    }

    public GetEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
