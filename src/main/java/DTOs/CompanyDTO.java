package DTOs;

import Entities.Company;
import Entities.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record CompanyDTO(long id, String name, int totalShares, int vacantShares, int keySharesThreshold,
                         long money, long sharePrice, Set<User> users) implements EntityDTO<Company> {
    @JsonCreator
    public CompanyDTO(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("totalShares") int totalShares,
            @JsonProperty("vacantShares") int vacantShares,
            @JsonProperty("keySharesThreshold") int keySharesThreshold,
            @JsonProperty("money") long money,
            @JsonProperty("sharePrice") long sharePrice,
            @JsonProperty("users") Set<User> users
    ) {
        this.id = id;
        this.name = name;
        this.totalShares = totalShares;
        this.vacantShares = vacantShares;
        this.keySharesThreshold = keySharesThreshold;
        this.money = money;
        this.sharePrice = sharePrice;
        this.users = users == null ? new HashSet<>() : users;
    }

    public CompanyDTO(Company company) {
        this(company.getId(), company.getCompanyName(), company.getTotalShares(), company.getVacantShares(),
                company.getKeyShareholderThreshold(), company.getMoney(), company.getSharePrice(), company.getCopyOfUsers());
    }

    @Override
    public Company convertToTargetObject(String password) {
        return new Company(id, name, totalShares, vacantShares, keySharesThreshold, money, sharePrice, users, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
