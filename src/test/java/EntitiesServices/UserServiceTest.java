package EntitiesServices;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.NegativeMoneyException;
import InMemoryRepos.InMemoryUserRepository;
import Requests.CreateEntityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    public void setService() {
        // This test is for the service only so we get by with a simple in memory repo
        service = new UserService(new InMemoryUserRepository());
    }

    @Test
    public void dontCreateUserWithSameNamePassword() {
        CreateEntityRequest create1 = new CreateEntityRequest("name", "pass");
        CreateEntityRequest create2 = new CreateEntityRequest("name", "pass");

        service.create(create1);
        assertThrows(EntityDuplicatedException.class, () -> service.create(create2));
    }

    @Test
    public void cannotSetNegativeMoney() {
        CreateEntityRequest create1 = new CreateEntityRequest("name", "pass");
        long id = service.create(create1);
        service.updateMoney(id, +100);
        assertThrows(NegativeMoneyException.class, () -> service.updateMoney(id, -200));
    }
}