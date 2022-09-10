package com.vid.scraper.repository;

import com.vid.scraper.model.entity.RecommendationView;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendationViewRepository extends JpaRepository<RecommendationView, Long> {
    Optional<RecommendationView> findByUserAndVideo(User user, Video video);

    void deleteAllByVideo(Video video);
}
