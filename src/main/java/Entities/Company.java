package Entities;

import DTOs.UserDTO;

import java.util.*;

public class Company implements StoredById {
    private final long id;
    private final String companyName;
    private final int totalShares;
    private final int vacantShares;
    private final float keyShareholderThreshold;
    private final long money;

    private final long sharePrice;
    private final Set<User> users;

    public Company(long id, String companyName, int totalShares, float keyShareholderThreshold,
                   long money, long sharePrice) {
        this(id, companyName, totalShares, totalShares, keyShareholderThreshold, money, sharePrice);
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   float keyShareholderThreshold, long money, long sharePrice) {
        this(id, companyName, totalShares, vacantShares, keyShareholderThreshold, money, sharePrice, new HashSet<>());
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   float keyShareholderThreshold, long money, long sharePrice, Set<User> users) {
        this.id = id;
        this.companyName = companyName;
        this.totalShares = totalShares;
        this.vacantShares = vacantShares;
        this.keyShareholderThreshold = keyShareholderThreshold;
        this.money = money;
        this.sharePrice = sharePrice;
        this.users = users;
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

    public float getKeyShareholderThreshold() {
        return keyShareholderThreshold;
    }

    public long getMoney() {
        return money;
    }
    public long getSharePrice() {
        return sharePrice;
    }

    public List<UserDTO> getUsersDTOs() {
        List<UserDTO> usersDTOs = new ArrayList<>();
        for (User user : users) {
            usersDTOs.add(new UserDTO(user));
        }
        return usersDTOs;
    }

    public Company withName(String name) {
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("Name cannot be empty");

        return new Company(id, name, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, users);
    }
    public Company withCountShares(int countShares) {
        if (countShares < this.totalShares)
            throw new IllegalArgumentException("Total number of shares cannot decrease");

        return new Company(id, companyName, countShares, countShares, keyShareholderThreshold,
                money, sharePrice, users);
    }
    public Company withThreshold(float threshold) {
        if (threshold <= 0 || threshold > 1)
            throw new IllegalArgumentException("Key shareholder threshold must lie within (0, 1]");

        return new Company(id, companyName, totalShares, vacantShares, threshold, money,
                sharePrice, users);
    }
    public Company withMoney(long money) {
        if (money < 0)
            throw new IllegalArgumentException("Amount of money must be non-negative");

        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, users);
    }

    public Company withVacantSharesDelta(int deltaShares) {
        return new Company(id, companyName, totalShares, vacantShares + deltaShares,
                keyShareholderThreshold, money, sharePrice, users);
    }

    public Company withNewUser(User user) {
        Set<User> newUsers = new HashSet<>(users);
        newUsers.add(user);
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers);
    }

    public Company withoutUser(User user) {
        Set<User> newUsers = new HashSet<>(users);
        if (!newUsers.remove(user))
            throw new IllegalArgumentException();
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers);
    }

    public List<User> getKeyShareholders() {
        List<User> keyShareholders = new ArrayList<>();
        for (var holder : users) {
            if ((float) holder.getShares().get(id) / totalShares >= keyShareholderThreshold) {
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
}
