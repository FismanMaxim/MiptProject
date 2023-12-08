package Requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AddUserSharesRequest(List<ShareDelta> sharesDelta) {
    @JsonCreator
    public AddUserSharesRequest(
            @JsonProperty("sharesDelta") List<ShareDelta> sharesDelta) {
        this.sharesDelta = sharesDelta;
    }
}
