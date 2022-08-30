package com.vid.scraper.model;

import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    private Long id;
    private String title;
    private List<ResolutionDto> resolutions;
    private String imageUrl;
    private String description;
    private Integer views = 0;
    private List<TagDto> tags;
    private String createdAt;
    private Integer likes;
    private Integer dislikes;
    private Boolean isLiked;
    private Boolean isDisliked;

    public static VideoDto from(Video video) {
        VideoDto result = new VideoDto();
        result.id = video.getId();
        if (video.getHdUrl() == null && video.getFullHdUrl() == null) {
            result.resolutions = Arrays.asList(new ResolutionDto(video.getSdUrl(), 360));
        } else if (video.getHdUrl() != null && video.getFullHdUrl() == null) {
            result.resolutions = Arrays.asList(new ResolutionDto(video.getSdUrl(), 360), new ResolutionDto(video.getHdUrl(), 720));
        } else if (video.getHdUrl() != null && video.getFullHdUrl() != null) {
            result.resolutions = Arrays.asList(new ResolutionDto(video.getSdUrl(), 360), new ResolutionDto(video.getHdUrl(), 720), new ResolutionDto(video.getFullHdUrl(), 1080));
        }
        result.imageUrl = video.getImageUrl();
        result.description = video.getDescription();
        result.title = video.getTitle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        result.createdAt = video.getCreatedAt().format(formatter);
        result.views = video.getCountOfViews();
        Map<String, Integer> countOfLikesAndDislikes = video.getCountOfLikesAndDislikes();
        result.likes = countOfLikesAndDislikes.get("likes");
        result.dislikes = countOfLikesAndDislikes.get("dislikes");
        return result;
    }

    public static VideoDto fromWithTags(Video video) {
        VideoDto videoDto = from(video);
        List<TagDto> tagDtos = new ArrayList<>();
        video.getTags().forEach(tag -> tagDtos.add(TagDto.from(tag)));
        videoDto.tags = tagDtos;
        return videoDto;
    }

    public static VideoDto fromWithIsLiked(Video video, User user) {
        VideoDto result = fromWithTags(video);
        result.isLiked = video.isLiked(user, 1);
        result.isDisliked = video.isLiked(user, -1);
        return result;
    }
}
