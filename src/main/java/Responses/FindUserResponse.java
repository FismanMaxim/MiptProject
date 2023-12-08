package Responses;

import DTOs.UserDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record FindUserResponse(UserDTO user) {

    @JsonCreator
    public FindUserResponse(
            @JsonProperty("user") UserDTO user) {
        this.user = user;
    }
}
