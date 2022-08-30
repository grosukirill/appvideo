package com.vid.scraper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    @JsonCreator
    public ChangePasswordRequest(@JsonProperty("oldPassword") String oldPassword,
                                 @JsonProperty("newPassword") String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
