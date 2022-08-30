package com.vid.scraper.service;

import com.vid.scraper.model.entity.User;

import java.util.Map;

public interface TokenService {
    String generate(User user);

    Map<String, String> getUserDataFromToken(String rawToken);
}
