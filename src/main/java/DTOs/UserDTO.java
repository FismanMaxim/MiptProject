package DTOs;

import Entities.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record UserDTO(String name, double money, Map<Long, Integer> shares) implements EntityDTO<User> {
    @JsonCreator
    public UserDTO(
            @JsonProperty("name") String name,
            @JsonProperty("money") double money,
            @JsonProperty("shares") Map<Long, Integer> shares
    ) {
        this.name = name;
        this.money = money;
        this.shares = shares;
    }

    public UserDTO(User user) {
        this(user.getUserName(), user.getMoney(), user.getShares());
    }

    @Override
    public User convertToTargetObject(long id) {
        return new User(id, name, money);
    }
}
