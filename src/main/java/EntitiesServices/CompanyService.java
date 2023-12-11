package EntitiesServices;

import CustomExceptions.CreateEntityException;
import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityNotFoundException;
import CustomExceptions.GetEntityException;
import Entities.Company;
import EntitiesRepositories.EntityRepository;
import Requests.CreateEntityRequest;

import java.util.List;

public class CompanyService extends EntityService<Company> implements StoredByNamePasswordService<Company> {
    public CompanyService(EntityRepository<Company> repository) {
        super(repository);
    }


    public List<Company> getAll() {
        try {
            return repository.getAll();
        } catch (EntityNotFoundException e) {
            throw new GetEntityException("Cannot get all companies");
        }
    }

    @Override
    public long create(CreateEntityRequest createRequest) {
        if (isNamePasswordPresent(createRequest.getName(), createRequest.getPassword()))
            throw new EntityDuplicatedException("User name/password duplicates");

        long id = repository.generateId();
        Company company = new Company(id, createRequest.getName(), 0,  0, 0, 0, createRequest.getPassword());
        try {
            repository.create(company);
        } catch (EntityDuplicatedException e) {
            throw new CreateEntityException(
                    "Cannot create entity because it duplicated existing one, id=" + company.getId(), e);
        }
        return id;
    }

    @Override
    public Company getByNamePassword(String name, String password) {
        return repository.getByNamePassword(name, password);
    }
}
