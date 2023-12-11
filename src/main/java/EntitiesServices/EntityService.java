package EntitiesServices;

import CustomExceptions.*;
import DTOs.EntityDTO;
import Entities.StoredById;
import EntitiesRepositories.EntityRepository;
import Requests.CreateEntityRequest;

public abstract class EntityService<T extends StoredById> {
    protected final EntityRepository<T> repository;


    public EntityService(EntityRepository<T> repository) {
        this.repository = repository;
    }

    public T getById(long id) {
        try {
            return repository.getById(id);
        } catch (EntityNotFoundException e) {
            throw new GetEntityException("Cannot get entity by id=" + id, e);
        }
    }

    private T getByNamePassword(String name, String password) {
        try {
            return repository.getByNamePassword(name, password);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    protected boolean isNamePasswordPresent(String name, String password) {
        return getByNamePassword(name, password) != null;
    }

    public abstract long create(CreateEntityRequest createRequest);

    public void update(T obj) {
        try {
            repository.update(obj);
        } catch (EntityNotFoundException e) {
            throw new UpdateEntityException("Cannot update entity with given id since it was not found, id=" + obj.getId(), e);
        }
    }

    public void delete(long id) {
        try {
            repository.delete(id);
        } catch (EntityNotFoundException e) {
            throw new DeleteEntityException("Cannot delete entity with given id since it was not found, id=" + id, e);
        }
    }
}
