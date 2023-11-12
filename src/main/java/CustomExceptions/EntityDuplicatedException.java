package CustomExceptions;

public class EntityDuplicatedException extends Exception {
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
