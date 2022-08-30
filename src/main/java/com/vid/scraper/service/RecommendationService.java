package com.vid.scraper.service;

import com.vid.scraper.model.entity.Like;
import com.vid.scraper.model.entity.RecommendationView;
import com.vid.scraper.model.entity.User;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.List;

public interface RecommendationService {
    List<RecommendedItem> findRecommendations(User user, List<Like> likes, List<RecommendationView> views, Integer numberOfRecommendations);

}
