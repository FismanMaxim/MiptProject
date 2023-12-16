package DTOs;

import Entities.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public record UserDTO(long id, String name, double money, Map<Long, Integer> shares) implements EntityDTO<User> {
    @JsonCreator
    public UserDTO(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("money") double money,
            @JsonProperty("shares") Map<Long, Integer> shares
    ) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.shares = shares;
    }

    public UserDTO(User user) {
        this(user.getId(), user.getUserName(), user.getMoney(), user.getCopyOfShares());
    }

    @Override
    public User convertToTargetObject(String password) {
        return new User(id, name, money, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return id == userDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
