package com.vid.scraper.service;

import com.vid.scraper.model.Response;

public interface SearchService {
    Response elasticSearch(String search);

    Response searchVideos(String search, String type, Integer page);
}
