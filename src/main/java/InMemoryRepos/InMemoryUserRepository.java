package InMemoryRepos;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityIdNotFoundException;
import Entities.User;
import EntitiesRepositories.EntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements EntityRepository<User> {
    private final AtomicLong nextId = new AtomicLong(0);

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public long generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) throws EntityIdNotFoundException {
        User user = users.get(id);
        if (user == null)
            throw new EntityIdNotFoundException();
        return user;
    }

    @Override
    public void create(User user) throws EntityDuplicatedException {
        if (users.get(user.getId()) != null)
            throw new EntityDuplicatedException();
        users.put(user.getId(),  user);
    }

    @Override
    public void update(User user) throws EntityIdNotFoundException {
        if (users.get(user.getId()) == null)
            throw new EntityIdNotFoundException();
        users.put(user.getId(),  user);
    }

    @Override
    public void delete(long id) throws EntityIdNotFoundException {
        if (users.remove(id) == null)
            throw new EntityIdNotFoundException();
    }
}
