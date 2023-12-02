package Entities;

import DTOs.UserDTO;

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
    private final Set<UserDTO> users;

    public Company(long id, String companyName, int totalShares, int keyShareholderThreshold,
                   long money, long sharePrice, String password) {
        this(id, companyName, totalShares, totalShares, keyShareholderThreshold, money, sharePrice, password);
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   int keyShareholderThreshold, long money, long sharePrice, String password) {
        this(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, new HashSet<>(), password);
    }

    public Company(long id, String companyName, int totalShares, int vacantShares,
                   int keyShareholderThreshold, long money, long sharePrice, Set<UserDTO> users, String password) {
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

    public long getMoney() {
        return money;
    }
    public long getSharePrice() {
        return sharePrice;
    }

    public Set<UserDTO> getUsersDTOs() {
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
    public Company withMoney(long money) {
        if (money < 0)
            throw new IllegalArgumentException("Amount of money must be non-negative");

        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, users, password);
    }

    public Company withVacantSharesDelta(int deltaShares) {
        return new Company(id, companyName, totalShares, vacantShares + deltaShares,
                keyShareholderThreshold, money, sharePrice, users, password);
    }

    public Company withNewUser(User user) {
        Set<UserDTO> newUsers = new HashSet<>(users);
        newUsers.add(new UserDTO(user));
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers, password);
    }

    public Company withoutUser(User user) {
        Set<UserDTO> newUsers = new HashSet<>(users);
        if (!newUsers.remove(new UserDTO(user)))
            throw new IllegalArgumentException();
        return new Company(id, companyName, totalShares, vacantShares, keyShareholderThreshold,
                money, sharePrice, newUsers, password);
    }

    public List<UserDTO> getKeyShareholders() {
        List<UserDTO> keyShareholders = new ArrayList<>();
        for (var holder : users) {
            if (holder.shares().get(id) >= keyShareholderThreshold) {
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
