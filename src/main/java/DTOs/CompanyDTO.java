package DTOs;

import Entities.Company;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record CompanyDTO(String name, int totalShares, int vacantShares, int keySharesThreshold,
                         long money, long sharePrice, String password, Set<UserDTO> users) implements EntityDTO<Company> {
    @JsonCreator
    public CompanyDTO(
            @JsonProperty("name") String name,
            @JsonProperty("totalShares") int totalShares,
            @JsonProperty("vacantShares") int vacantShares,
            @JsonProperty("keySharesThreshold") int keySharesThreshold,
            @JsonProperty("money") long money,
            @JsonProperty("sharePrice") long sharePrice,
            @JsonProperty("password") String password,
            @JsonProperty("users") Set<UserDTO> users
    ) {
        this.name = name;
        this.totalShares = totalShares;
        this.vacantShares = vacantShares;
        this.keySharesThreshold = keySharesThreshold;
        this.money = money;
        this.sharePrice = sharePrice;
        this.password = password;
        this.users = users == null ? new HashSet<>() : users;
    }

    public CompanyDTO(Company company) {
        this(company.getCompanyName(), company.getTotalShares(), (company.getVacantShares()),
                company.getKeyShareholderThreshold(), company.getMoney(), company.getSharePrice(),
                company.getPassword(), company.getUsersDTOs());
    }

    @Override
    public Company convertToTargetObject(long id) {
        return new Company(id, name, totalShares, vacantShares, keySharesThreshold, money, sharePrice, users, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }
}
