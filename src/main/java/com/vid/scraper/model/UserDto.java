package com.vid.scraper.model;

import com.vid.scraper.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;

    public static UserDto from(User user) {
        UserDto result = new UserDto();
        result.id = user.getId();
        result.username = user.getName();
        result.email = user.getEmail();
        return result;
    }
}
