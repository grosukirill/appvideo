package com.vid.scraper.model;

import com.vid.scraper.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {
    private Long id;
    private String name;
    private String email;
    private String token;

    public static LoginDto from(User user, String token) {
        LoginDto result = new LoginDto();
        result.id = user.getId();
        result.email = user.getEmail();
        result.name = user.getName();
        result.token = token;
        return result;
    }
}
