package Entities;

import CustomExceptions.NegativeSharesException;
import Requests.ShareDelta;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class User implements StoredById {
    private final long id;
    private final String userName;
    private final double money;
    /**
     * Stores how many shares a user possesses of a specific company<br>
     * <i>Key</i> is index of the company, <i>value</i> is the number of shares
     */
    private final Map<Long, Integer> shares;

    private final String password;


    public User(long id, String userName, double money, String password) {
        this(id, userName, money, new HashMap<>(), password);
    }

    @JsonCreator
    public User(
            @JsonProperty("id") long id,
            @JsonProperty("userName") String userName,
            @JsonProperty("money") double money,
            @JsonProperty("shares") Map<Long, Integer> shares,
            @JsonProperty("password") String password) {
        this.id = id;
        this.userName = userName;
        this.money = money;
        this.shares = shares;
        this.password = password;
    }

    public User(long id, String userName, double money) {
        this(id, userName, money, new HashMap<>(), "default");
    }

    public User(long id, String userName, double money, Map<Long, Integer> shares) {
        this(id, userName, money, shares, "default");
    }

    @Override
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Map<Long, Integer> getShares() {
        return shares;
    }

    public double getMoney() {
        return money;
    }

    @JsonIgnore
    public Map<Long, Integer> getCopyOfShares() {
        return new HashMap<>(shares);
    }

    public int countSharesOfCompany(Long companyId) {
        return shares.getOrDefault(companyId, 0);
    }

    @JsonIgnore
    public Set<Long> getIdsOfCompaniesWithShares() {
        return new HashSet<>(shares.keySet());
    }

    public User withName(String name) {
        return new User(id, name, money, new HashMap<>(shares), password);
    }

    public User withMoney(double money) {
        return new User(id, userName, money, new HashMap<>(shares), password);
    }

    public User withSharesDelta(List<ShareDelta> sharesDelta) throws NegativeSharesException {
        Map<Long, Integer> newShares = new HashMap<>(shares);

        for (ShareDelta shareDelta : sharesDelta) {
            long companyId = shareDelta.companyId();
            if (newShares.containsKey(companyId)) {
                newShares.put(companyId, newShares.get(companyId) + shareDelta.countDelta());
            } else {
                newShares.put(companyId, shareDelta.countDelta());
            }

            if (newShares.get(companyId) < 0) {
                throw new NegativeSharesException();
            }
        }

        return new User(id, userName, money, newShares, password);
    }

    public User withPassword(String password) {
        return new User(id, userName, money, new HashMap<>(shares), password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + userName + '\'' +
                ", money=" + money +
                ", shares=" + shares +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getPassword() {
        return password;
    }

}
