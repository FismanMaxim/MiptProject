package CustomExceptions;

public class EntityIdNotFoundException extends RuntimeException {

    public EntityIdNotFoundException() {
        super();
    }

    public EntityIdNotFoundException(String message) {
        super(message);
    }

    public EntityIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
