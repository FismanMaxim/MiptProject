package EntitiesServices;

import CustomExceptions.NegativeMoneyException;
import Entities.User;
import EntitiesRepositories.EntityRepository;
import Requests.ShareDelta;

import java.util.List;

public class UserService extends EntityService<User> implements StoredByNamePasswordService<User> {

    public UserService(EntityRepository<User> repository) {
        super(repository);
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
