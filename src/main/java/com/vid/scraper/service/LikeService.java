package com.vid.scraper.service;


import com.vid.scraper.model.CreateLikeRequest;
import com.vid.scraper.model.Response;

public interface LikeService {
    Response like(String rawToken, CreateLikeRequest request);

    Response findLikeVideos(String rawToken, Integer page);
}
