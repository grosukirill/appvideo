package com.vid.scraper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;

    @JsonCreator
    public RegisterRequest(@JsonProperty("name") String name,
                           @JsonProperty("email") String email,
                           @JsonProperty("password") String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
