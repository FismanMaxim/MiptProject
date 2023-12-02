package Requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationRequest(String name, String password) {
    @JsonCreator
    public AuthenticationRequest(
            @JsonProperty String name,
            @JsonProperty String password) {
        this.name = name;
        this.password = password;
    }

}
