package Requests;

import DTOs.EntityDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Year;

public class CreateEntityRequest {
    protected final String name;
    protected final String password;

    @JsonCreator
    public CreateEntityRequest(
            @JsonProperty("name") String name,
            @JsonProperty("password") String password
    ) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
