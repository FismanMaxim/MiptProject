package EntitiesServices;

import CustomExceptions.CreateEntityException;
import CustomExceptions.EntityDuplicatedException;
import CustomExceptions.NegativeMoneyException;
import Entities.User;
import EntitiesRepositories.EntityRepository;
import Requests.CreateEntityRequest;
import Requests.ShareDelta;

import java.util.List;

public class UserService extends EntityService<User> implements StoredByNamePasswordService<User> {

    public UserService(EntityRepository<User> repository) {
        super(repository);
    }

    @Override
    public long create(CreateEntityRequest createRequest) {
        if (isNamePasswordPresent(createRequest.getName(), createRequest.getPassword()))
            throw new EntityDuplicatedException("User name/password duplicates");

        long id = repository.generateId();
        User user = new User(id, createRequest.getName(), 0, createRequest.getPassword());
        try {
            repository.create(user);
        } catch (EntityDuplicatedException e) {
            throw new CreateEntityException(
                    "Cannot create entity because it duplicated existing one, id=" + user.getId(), e);
        }
        return id;
    }

    public void updateMoney(long id, double deltaMoney) {
        User user = repository.getById(id);
        double userMoney = user.getMoney() + deltaMoney;

        if (userMoney < 0)
            throw new NegativeMoneyException();

        user = user.withMoney(userMoney);
        repository.update(user);
    }

    public void updateName(long id, String newName) {
        User user = repository.getById(id);
        user = user.withName(newName);
        repository.update(user);
    }

    public void updateShares(long id, List<ShareDelta> sharesDelta) {
        User user = repository.getById(id);
        user = user.withSharesDelta(sharesDelta);
        repository.update(user);
    }

    @Override
    public User getByNamePassword(String name, String password) {
        return repository.getByNamePassword(name, password);
    }
}
