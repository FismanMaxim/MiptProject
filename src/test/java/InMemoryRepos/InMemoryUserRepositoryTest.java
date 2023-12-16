package InMemoryRepos;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityNotFoundException;
import Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    public void setRepository () {
        repository = new InMemoryUserRepository();
    }

    @Test
    public void cannotCreateDuplicatedId() {
        User user1 = new User(0, "name", 0, "pass");
        User user2 = new User(0, "name1", 0, "pass1");
        repository.create(user1);
        assertThrows(EntityDuplicatedException.class, () -> repository.create(user2));
    }

    @Test
    public void cannotUpdateNotCreatedCompany() {
        User notCreatedUser = new User(1, "name", 0, "pass");
        assertThrows(EntityNotFoundException.class, () -> repository.update(notCreatedUser));
    }

    @Test
    public void cannotDeleteNotCreatedCompany() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(0));
    }
}