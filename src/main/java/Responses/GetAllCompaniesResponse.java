package Responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record  GetAllCompaniesResponse (List<FindCompanyResponse> companiesResponses) {
    @JsonCreator
    public GetAllCompaniesResponse(
            @JsonProperty List<FindCompanyResponse> companiesResponses) {
        this.companiesResponses = companiesResponses;
    }
}
