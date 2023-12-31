package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Company implements StoredById {
    private final long id;
    private final String companyName;
    private final int totalShares;
    private final int vacantShares;
    private final int keyShareholderThreshold;
    private final long money;
    private final String password;

    private final long sharePrice;
    private final Set<User> users;

    public Company(long id, String companyName, int totalShares, int keyShareholderThreshold,
                   long money, long sharePrice, String password) {
        this(id, companyName, totalShares, totalShares, keyShareholderThreshold, money, sharePrice, password);
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   int keyShareholderThreshold, long money, long sharePrice, String password) {
        this(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, new HashSet<>(), password);
    }

    @JsonCreator
    public Company(
            @JsonProperty("id") long id,
            @JsonProperty("companyName") String companyName,
            @JsonProperty("totalShares") int totalShares,
            @JsonProperty("vacantShares") int vacantShares,
            @JsonProperty("keyShareholderThreshold") int keyShareholderThreshold,
            @JsonProperty("money") long money,
            @JsonProperty("sharePrice") long sharePrice,
            @JsonProperty("users") Set<User> users,
            @JsonProperty("password") String password) {
        this.id = id;
        this.companyName = companyName;
        this.totalShares = totalShares;
        this.vacantShares = vacantShares;
        this.keyShareholderThreshold = keyShareholderThreshold;
        this.money = money;
        this.sharePrice = sharePrice;
        this.users = users;
        this.password = password;
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   float keyShareholderThresholdPercentage, long money, long sharePrice, Set<User> users, String password) {
        this(id, companyName, totalShares, vacantShares,
                (int) Math.ceil(keyShareholderThresholdPercentage * totalShares), money, sharePrice, users, password);
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   float keyShareholderThresholdPercentage, long money, long sharePrice, Set<User> users) {
        this(id, companyName, totalShares, vacantShares,
                (int) Math.ceil(keyShareholderThresholdPercentage * totalShares), money, sharePrice, users, "default");
    }

    public Company(long id, String companyName, int totalShares, int keyShareholderThreshold,
                   long money, long sharePrice) {
        this(id, companyName, totalShares, totalShares,
                keyShareholderThreshold, money, sharePrice, "default");
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   int keyShareholderThreshold, long money, long sharePrice) {
        this(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, new HashSet<>(), "default");
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   int keyShareholderThreshold, long money, long sharePrice, Set<User> users) {
        this(id, companyName, totalShares, vacantShares, keyShareholderThreshold, money,
                sharePrice, users, "default");
    }

    @Override
    public long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getTotalShares() {
        return totalShares;
    }

    public int getVacantShares() {
        return vacantShares;
    }

    public int getKeyShareholderThreshold() {
        return keyShareholderThreshold;
    }

    public Set<User> getUsers() {
        return Set.copyOf(users);
    }

    public long getMoney() {
        return money;
    }

    public long getSharePrice() {
        return sharePrice;
    }

    @JsonIgnore
    public List<Long> getUserIds() {
        List<Long> list = new ArrayList<>();
        for (var i : users) {
            list.add(i.getId());
        }
        return list;
    }

    @JsonIgnore
    public Set<User> getCopyOfUsers() {
        return new HashSet<>(users);
    }

    public Company withName(String name) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("Name cannot be empty");

        return new Company(id, name, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, users, password);
    }

    public Company withCountShares(int countShares, boolean preserveVacantShares) {
        if (countShares < this.totalShares)
            throw new IllegalArgumentException("Total number of shares cannot decrease");

        return new Company(id, companyName, countShares, preserveVacantShares ? vacantShares : countShares, keyShareholderThreshold,
                money, sharePrice, users, password);
    }

    public Company withThreshold(int threshold) {
        if (threshold <= 0 || threshold > totalShares)
            throw new IllegalArgumentException("Key shareholder threshold must lie within (0, totalShares]");

        return new Company(id, companyName, totalShares, vacantShares, threshold, money,
                sharePrice, users, password);
    }

    public Company withDeltaMoney(long deltaMoney) {
        if (money + deltaMoney < 0)
            throw new IllegalArgumentException("Amount of money must be non-negative");

        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money + deltaMoney, sharePrice, users, password);
    }

    public Company withSharePrice(long sharePrice) {
        if (sharePrice < 0)
            throw new IllegalArgumentException("Share price cannot be illegal");

        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, users, password);
    }

    public Company withVacantSharesDelta(int deltaShares) {
        return new Company(id, companyName, totalShares, vacantShares + deltaShares,
                keyShareholderThreshold, money, sharePrice, users, password);
    }

    public Company withNewUser(User user) {
        Set<User> newUsers = new HashSet<>(users);
        newUsers.add(user);
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers, password);
    }

    public Company withoutUser(User user) {
        Set<User> newUsers = new HashSet<>(users);
        if (!newUsers.remove(user))
            throw new IllegalArgumentException();
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers, password);
    }

    @JsonIgnore
    public List<User> getKeyShareholders() {
        List<User> keyShareholders = new ArrayList<>();
        for (var holder : users) {
            if (holder.countSharesOfCompany(id) >= keyShareholderThreshold) {
                keyShareholders.add(holder);
            }
        }
        return keyShareholders;
    }

    public boolean hasUser(User user) {
        return users.contains(user);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", totalShares=" + totalShares +
                ", vacantShares=" + vacantShares +
                ", keyShareholderThreshold=" + keyShareholderThreshold +
                ", money=" + money +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id == company.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getPassword() {
        return password;
    }
}
