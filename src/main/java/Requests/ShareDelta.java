package Requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShareDelta (Long companyId, Integer countDelta) {
    @JsonCreator
    public ShareDelta(
            @JsonProperty("companyId") Long companyId,
            @JsonProperty("countDelta") Integer countDelta) {
        this.companyId = companyId;
        this.countDelta = countDelta;
    }
}
