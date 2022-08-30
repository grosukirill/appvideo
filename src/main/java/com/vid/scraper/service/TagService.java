package com.vid.scraper.service;

import com.vid.scraper.model.Response;
import com.vid.scraper.model.VideoByTagResponse;
import org.springframework.stereotype.Service;

@Service
public interface TagService {
    Response findPopular();

    Response findAll(Integer pageNumber);

    VideoByTagResponse findAllVideosByTag(Long tagId, Integer page);
}
