package com.vid.scraper.service;

import com.vid.scraper.exception.UserNotFoundException;
import com.vid.scraper.exception.VideoNotFoundException;
import com.vid.scraper.model.*;
import com.vid.scraper.model.entity.Like;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import com.vid.scraper.repository.LikeRepository;
import com.vid.scraper.repository.UserRepository;
import com.vid.scraper.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final TokenService tokenService;

    @Override
    @Transactional
    public Response like(String rawToken, CreateLikeRequest request) {
        User user = getUserFromToken(rawToken);
        Optional<Video> video = videoRepository.findById(request.getVideoId());
        if (!video.isPresent()) {
            throw new VideoNotFoundException(String.format("Video with ID [%s] not found", request.getVideoId()));
        }
        Optional<Like> foundLike = likeRepository.findByUserAndVideoAndLikeType(user, video.get(), request.getLike());
        if (foundLike.isPresent()) {
            likeRepository.delete(foundLike.get());
            video.get().removeLike(user, request.getLike());
            Video updatedVideo = videoRepository.save(video.get());
            VideoSummaryDto videoSummaryDto = VideoSummaryDto.from(updatedVideo);
            return new Response(true, videoSummaryDto, null);
        }
        Optional<Like> foundInverseLike = likeRepository.findByUserAndVideoAndLikeType(user, video.get(), -request.getLike());
        if (foundInverseLike.isPresent()) {
            likeRepository.delete(foundInverseLike.get());
            video.get().removeLike(user, -request.getLike());
            videoRepository.save(video.get());
        }
        Like like = likeRepository.save(Like.builder()
                .likeType(request.getLike())
                .user(user)
                .video(video.get())
                .createdAt(OffsetDateTime.now())
                .build());
        video.get().getLikes().add(like);
        Video updatedVideo = videoRepository.save(video.get());
        VideoSummaryDto videoSummaryDto = VideoSummaryDto.fromWithIsLiked(updatedVideo, user);
        return new Response(true, videoSummaryDto, null);
    }

    @Override
    @Transactional
    public Response findLikeVideos(String rawToken, Integer page) {
        User user = getUserFromToken(rawToken);
        int offset = page * 20;
        List<Video> videos = videoRepository.findVideosByLiked(user.getId(), offset);
        List<VideoSummaryDto> videoDtos = new ArrayList<>();
        videos.forEach(video -> videoDtos.add(VideoSummaryDto.from(video)));
        PaginationDto paginationDto = new PaginationDto(true, page+1);
        if (videos.size() < 20) {
            paginationDto.setHasNextPage(false);
            paginationDto.setNextPage(page);
        }
        return new Response(true, videoDtos, paginationDto);
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
