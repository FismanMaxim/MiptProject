package EntitiesRepositories;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityIdNotFoundException;
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
     * @throws EntityIdNotFoundException if the repository contains no object with the given id
     */
    T getById(long id) throws EntityIdNotFoundException;

    /**
     * Creates a new object in the repository with corresponding fields
     * @param obj The object to create
     * @throws EntityDuplicatedException if the repository already contains an object with the same id as obj
     */
    void create(T obj) throws EntityDuplicatedException;

    /**
     * Updates an object in the repository
     * @param obj Information about the object to be updated
     * @throws EntityIdNotFoundException If the object with corresponding id was not found
     */
    void update(T obj) throws EntityIdNotFoundException;

    /**
     * Deletes an object with the given id from the repository
     * @param id The id of the object to delete
     * @throws EntityIdNotFoundException If the repository contains no object with the given id
     */
    void delete(long id) throws EntityIdNotFoundException;
}
