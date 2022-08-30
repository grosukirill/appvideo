package com.vid.scraper.service;

import com.vid.scraper.model.EditUserRequest;
import com.vid.scraper.model.LoginDto;
import com.vid.scraper.model.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponse findUserByToken(String rawToken);

    LoginDto editUser(String rawToken, EditUserRequest request);
}
