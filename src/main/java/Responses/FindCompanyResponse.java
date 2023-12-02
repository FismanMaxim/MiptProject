package Responses;

import DTOs.CompanyDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record FindCompanyResponse (CompanyDTO companyDTO) {
    @JsonCreator
    public FindCompanyResponse(
            @JsonProperty("companyDTO") CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }
}
