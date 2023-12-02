package EntitiesServices;

public interface StoredByNamePasswordService<T> {
    T getByNamePassword(String name, String password);
}
