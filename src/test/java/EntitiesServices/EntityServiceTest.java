package EntitiesServices;

import CustomExceptions.DeleteEntityException;
import CustomExceptions.UpdateEntityException;
import Entities.User;
import InMemoryRepos.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityServiceTest {
    EntityService<User> userService;

    @BeforeEach
    public void setService() {
        userService = new UserService(new InMemoryUserRepository());
    }

    @Test
    void updateNonExistingObject() {
        User notAddedUser = new User(0, "name", 0, "password");
        assertThrows(UpdateEntityException.class, () -> userService.update(notAddedUser));
    }

    @Test
    void deleteNonExistingObject() {
        assertThrows(DeleteEntityException.class, () -> userService.delete(0));
    }
}