package com.vid.scraper.service;

import com.vid.scraper.exception.DuplicateViewsException;
import com.vid.scraper.exception.UserNotFoundException;
import com.vid.scraper.exception.VideoNotFoundException;
import com.vid.scraper.model.CreateRecommendationViewRequest;
import com.vid.scraper.model.entity.RecommendationView;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import com.vid.scraper.repository.RecommendationViewRepository;
import com.vid.scraper.repository.UserRepository;
import com.vid.scraper.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final RecommendationViewRepository recommendationViewRepository;

    @Override
    public void createRecommendationView(String rawToken, CreateRecommendationViewRequest request) {
        User user = getUserFromToken(rawToken);
        Optional<Video> video = videoRepository.findById(request.getVideoId());
        if (!video.isPresent()) {
            throw new VideoNotFoundException(String.format("Video with ID [%s] not found", request.getVideoId()));
        }
        Optional<RecommendationView> existingView = recommendationViewRepository.findByUserAndVideo(user, video.get());
        if (existingView.isPresent()) {
            throw new DuplicateViewsException(String.format("User with email [%s] already viewed video with ID [%s]", user.getEmail(), request.getVideoId()));
        }
        RecommendationView recommendationView = RecommendationView.builder().user(user).video(video.get()).createdAt(OffsetDateTime.now()).build();
        recommendationViewRepository.save(recommendationView);
    }

    private User getUserFromToken(String rawToken) {
        Map<String, String> userData = tokenService.getUserDataFromToken(rawToken);
        Optional<User> user = userRepository.findByEmail(userData.get("email"));
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User with token [%s] not found", rawToken));
        }
        return user.get();
    }
}
