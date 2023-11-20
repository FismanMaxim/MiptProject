package EntitiesServices;

import CustomExceptions.*;
import DTOs.EntityDTO;
import Entities.StoredById;
import EntitiesRepositories.EntityRepository;

public abstract class EntityService<T extends StoredById> {
    protected final EntityRepository<T> repository;


    public EntityService(EntityRepository<T> repository) {
        this.repository = repository;
    }

    public T getById(long id) {
        try {
            return repository.getById(id);
        } catch (EntityIdNotFoundException e) {
            throw new GetEntityException("Cannot get entity by id=" + id, e);
        }
    }

    public <T_DTO extends EntityDTO<T>> long create(T_DTO objDTO) {
        long id = repository.generateId();
        T obj = objDTO.convertToTargetObject(id);
        try {
            repository.create(obj);
        } catch (EntityDuplicatedException e) {
            throw new CreateEntityException(
                    "Cannot create entity because it duplicated existing one, id=" + obj.getId(), e);
        }
        return id;
    }

    public void update(T obj) {
        try {
            repository.update(obj);
        } catch (EntityIdNotFoundException e) {
            throw new UpdateEntityException("Cannot update entity with given id since it was not found, id=" + obj.getId(), e);
        }
    }

    public void delete(long id) {
        try {
            repository.delete(id);
        } catch (EntityIdNotFoundException e) {
            throw new DeleteEntityException("Cannot delete entity with given id since it was not found, id=" + id, e);
        }
    }
}
