package com.vid.scraper.service;

import com.vid.scraper.model.*;

public interface AuthenticationService {
    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void changePassword(String rawToken, ChangePasswordRequest request);
}
