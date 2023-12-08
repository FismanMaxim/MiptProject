package EntitiesRepositories;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityNotFoundException;
import Entities.StoredById;

import java.util.List;

public interface EntityRepository<T extends StoredById> {
    /**
     * Generates a vacant id that a new object can be created with
     * @return generated id
     */
    long generateId();

    /**
     * @return all the entities the repository contains
     */
    List<T> getAll();

    /**
     * Finds object with the given id in the repository
     * @return Returns the object with the given id
     * @throws EntityNotFoundException if the repository contains no object with the given id
     */
    T getById(long id);

    /**
     * Finds object whose name and password match the given
     * @throws EntityNotFoundException if the entity with given name and password was not found
     */
    T getByNamePassword(String name, String password);

    /**
     * Creates a new object in the repository with corresponding fields
     * @param obj The object to create
     * @throws EntityDuplicatedException if the repository already contains an object with the same id as obj
     */
    void create(T obj);

    /**
     * Updates an object in the repository
     * @param obj Information about the object to be updated
     * @throws EntityNotFoundException If the object with corresponding id was not found
     */
    void update(T obj);

    /**
     * Deletes an object with the given id from the repository
     * @param id The id of the object to delete
     * @throws EntityNotFoundException If the repository contains no object with the given id
     */
    void delete(long id);


}
