package Entities;

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


    public User(long id, String userName, double money) {
        this(id, userName, money, new HashMap<>());
    }
    public User(long id, String userName, double money, Map<Long, Integer> shares) {
        this.id = id;
        this.userName = userName;
        this.money = money;
        this.shares = shares;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public double getMoney() {
        return money;
    }

    public Map<Long, Integer> getShares() {
        return shares;
    }
}