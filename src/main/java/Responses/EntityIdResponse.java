package Responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EntityIdResponse(long id) {
    @JsonCreator
    public EntityIdResponse(
            @JsonProperty("id") long id) {
        this.id = id;
    }
}
