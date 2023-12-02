package DTOs;

import Entities.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record UserDTO(String name, double money, Map<Long, Integer> shares, String password) implements EntityDTO<User> {
    @JsonCreator
    public UserDTO(
            @JsonProperty("name") String name,
            @JsonProperty("money") double money,
            @JsonProperty("shares") Map<Long, Integer> shares,
            @JsonProperty("password") String password
    ) {
        this.name = name;
        this.money = money;
        this.shares = shares;
        this.password = password;
    }

    public UserDTO(String name, String password) {
        this(name, 0, new HashMap<>(), password);
    }

    public UserDTO(User user) {
        this(user.getUserName(), user.getMoney(), user.getShares(), user.getPassword());
    }

    @Override
    public User convertToTargetObject(long id) {
        return new User(id, name, money, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(name, userDTO.name) && Objects.equals(password, userDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }
}
