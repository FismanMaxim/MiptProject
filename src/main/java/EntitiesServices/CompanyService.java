package EntitiesServices;

import Entities.Company;
import EntitiesRepositories.EntityRepository;

public class CompanyService extends EntityService<Company> {
    public CompanyService(EntityRepository<Company> repository) {
        super(repository);
    }
}
