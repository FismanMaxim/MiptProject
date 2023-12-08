package InMemoryRepos;

import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.EntityNotFoundException;
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
    public User getById(long id) {
        User user = users.get(id);
        if (user == null)
            throw new EntityNotFoundException();
        return user;
    }

    @Override
    public User getByNamePassword(String name, String password) {
        for (var key : users.keySet())
            if (users.get(key).getUserName().equals(name) && users.get(key).getPassword().equals(password))
                return users.get(key);
        throw new EntityNotFoundException();
    }

    @Override
    public void create(User user) {
        if (users.get(user.getId()) != null)
            throw new EntityDuplicatedException();
        users.put(user.getId(),  user);
    }

    @Override
    public void update(User user) {
        if (users.get(user.getId()) == null)
            throw new EntityNotFoundException();
        users.put(user.getId(),  user);
    }

    @Override
    public void delete(long id) {
        if (users.remove(id) == null)
            throw new EntityNotFoundException();
    }
}
