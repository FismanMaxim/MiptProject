package Responses;

import Entities.Company;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record FindCompanyResponse (Company company) {
    @JsonCreator
    public FindCompanyResponse(
            @JsonProperty("company") Company company) {
        this.company = company;
    }
}
