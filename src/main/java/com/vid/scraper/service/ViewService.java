package com.vid.scraper.service;

import com.vid.scraper.model.CreateRecommendationViewRequest;

public interface ViewService {

    void createRecommendationView(String rawToken, CreateRecommendationViewRequest request);
}
