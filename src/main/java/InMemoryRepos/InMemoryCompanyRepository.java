package InMemoryRepos;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityIdNotFoundException;
import Entities.Company;
import EntitiesRepositories.EntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCompanyRepository implements EntityRepository<Company> {
    private final AtomicLong nextId = new AtomicLong(0);
    private final Map<Long, Company> companies = new ConcurrentHashMap<>();


    @Override
    public long generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public List<Company> getAll() {
        return new ArrayList<>(companies.values());
    }

    @Override
    public Company getById(long id) {
        Company company = companies.get(id);
        if (company == null)
            throw new EntityIdNotFoundException();
        return company;
    }

    @Override
    public synchronized void create(Company company) {
        if (companies.get(company.getId()) != null)
            throw new EntityDuplicatedException();
        companies.put(company.getId(), company);
    }

    @Override
    public synchronized void update(Company company) {
        if (companies.get(company.getId()) == null)
            throw new EntityIdNotFoundException();
        companies.put(company.getId(),  company);
    }

    @Override
    public void delete(long id) {
        if (companies.remove(id) == null)
            throw new EntityIdNotFoundException();
    }
}
