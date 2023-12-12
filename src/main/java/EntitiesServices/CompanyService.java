package EntitiesServices;

import CustomExceptions.EntityNotFoundException;
import CustomExceptions.GetEntityException;
import Entities.Company;
import EntitiesRepositories.EntityRepository;

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
    public Company getByNamePassword(String name, String password) {
        return repository.getByNamePassword(name, password);
    }
}
