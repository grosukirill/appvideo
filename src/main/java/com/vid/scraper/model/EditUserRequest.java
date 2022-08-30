package com.vid.scraper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EditUserRequest {
    private String name;
    private String email;

    @JsonCreator
    public EditUserRequest(@JsonProperty("username") String name,
                           @JsonProperty("email") String email) {
        this.name = name;
        this.email = email;
    }
}
