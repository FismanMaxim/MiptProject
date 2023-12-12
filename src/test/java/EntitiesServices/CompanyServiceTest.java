package EntitiesServices;

import CustomExceptions.EntityDuplicatedException;
import InMemoryRepos.InMemoryCompanyRepository;
import Requests.CreateEntityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyServiceTest {
    private CompanyService service;

    @BeforeEach
    public void setService() {
        // This test is for the service only so we get by with a simple in memory repo
        service = new CompanyService(new InMemoryCompanyRepository());
    }

    @Test
    public void dontCreateUserWithSameNamePassword() {
        CreateEntityRequest create1 = new CreateEntityRequest("name", "pass");
        CreateEntityRequest create2 = new CreateEntityRequest("name", "pass");

        service.create(create1);
        assertThrows(EntityDuplicatedException.class, () -> service.create(create2));
    }
}