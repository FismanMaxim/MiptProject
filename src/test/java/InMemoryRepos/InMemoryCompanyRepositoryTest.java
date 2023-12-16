package InMemoryRepos;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityNotFoundException;
import Entities.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryCompanyRepositoryTest {
    private InMemoryCompanyRepository repository;

    @BeforeEach
    public void setRepository () {
        repository = new InMemoryCompanyRepository();
    }

    @Test
    public void cannotCreateDuplicatedId() {
        Company company1 = new Company(1, "name", 0, 0, 0, 0, "pass");
        Company company2 = new Company(1, "name1", 0, 0, 0, 0, "pass1");
        repository.create(company1);
        assertThrows(EntityDuplicatedException.class, () -> repository.create(company2));
    }

    @Test
    public void cannotUpdateNotCreatedCompany() {
        Company notCreatedCompany = new Company(1, "name", 0, 0, 0, 0, "pass");
        assertThrows(EntityNotFoundException.class, () -> repository.update(notCreatedCompany));
    }

    @Test
    public void cannotDeleteNotCreatedCompany() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(0));
    }
}