package com.vid.scraper.service;

import com.vid.scraper.model.Response;
import org.springframework.stereotype.Service;

@Service
public interface VideoService {
    Response getVideos(Integer page);

//    void scrapeVideos();
//
//    void verifyAllVideos();

    Response getVideoById(Long videoId, String rawToken);

    Response getPopularVideos(Integer page, Integer perPage, Integer days);

    Response getRecommendations(String rawToken, Integer page, String videoIds);

    Response getSimilar(Long videoId, Integer page);
}
