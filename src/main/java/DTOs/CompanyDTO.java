package DTOs;

import Entities.Company;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CompanyDTO(String name, int totalShares, int vacantShares, float keySharesThreshold, long money, long sharePrice) implements EntityDTO<Company> {
    @JsonCreator
    public CompanyDTO(
            @JsonProperty("name") String name,
            @JsonProperty("shares") int totalShares,
            @JsonProperty("vacantShares") int vacantShares,
            @JsonProperty("threshold") float keySharesThreshold,
            @JsonProperty("money") long money,
            @JsonProperty("sharePrice") long sharePrice
    ) {
        this.name = name;
        this.totalShares = totalShares;
        this.vacantShares = vacantShares;
        this.keySharesThreshold = keySharesThreshold;
        this.money = money;
        this.sharePrice = sharePrice;
    }


    @Override
    public Company convertToTargetObject(long id) {
        return new Company(id, name, totalShares, keySharesThreshold, money, sharePrice);
    }
}
