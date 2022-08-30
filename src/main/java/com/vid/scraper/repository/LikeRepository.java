package com.vid.scraper.repository;

import com.vid.scraper.model.entity.Like;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndVideoAndLikeType(User user, Video video, Integer like);
}
