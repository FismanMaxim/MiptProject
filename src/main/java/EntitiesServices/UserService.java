package EntitiesServices;

import CustomExceptions.EntityIdNotFoundException;
import CustomExceptions.NegativeMoneyException;
import CustomExceptions.NegativeSharesException;
import Entities.User;
import EntitiesRepositories.EntityRepository;
import Requests.ShareDelta;

import java.util.List;

public class UserService extends EntityService<User> {

    public UserService(EntityRepository<User> repository) {
        super(repository);
    }

    public void updateMoney(long id, double deltaMoney)
            throws EntityIdNotFoundException, NegativeMoneyException {
        User user = repository.getById(id);
        double userMoney = user.getMoney() + deltaMoney;

        if (userMoney < 0)
            throw new NegativeMoneyException();

        user = user.withMoney(userMoney);
        repository.update(user);
    }

    public void updateName(long id, String newName) throws EntityIdNotFoundException {
        User user = repository.getById(id);
        user = user.withName(newName);
        repository.update(user);
    }

    public void updateShares(long id, List<ShareDelta> sharesDelta)
            throws EntityIdNotFoundException, NegativeSharesException {
        User user = repository.getById(id);
        user = user.withSharesDelta(sharesDelta);
        repository.update(user);
    }
}
