package CustomExceptions;

public class EntityDuplicatedException extends RuntimeException {
    public EntityDuplicatedException() {
        super();
    }

    public EntityDuplicatedException(String message) {
        super(message);
    }

    public EntityDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
