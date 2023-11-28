package Entities;

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
    public Set<User>getUsers(){
        return Set.copyOf(users);
    }
    public long getMoney() {
        return money;
    }
    public long getSharePrice() {
        return sharePrice;
    }
    public List<Long> getUserIds(){
        List<Long> list = new ArrayList<>();
        for(var i:users){
            list.add(i.getId());
        }
        return list;
    }

}