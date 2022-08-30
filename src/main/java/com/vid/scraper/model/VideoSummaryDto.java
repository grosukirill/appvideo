package com.vid.scraper.model;

import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.entity.Video;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class VideoSummaryDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String createdAt;
    private Integer views;
    private Integer likes;
    private Integer dislikes;
    private Boolean isLiked;
    private Boolean isDisliked;

    public static VideoSummaryDto from(Video video) {
        VideoSummaryDto result = new VideoSummaryDto();
        result.id = video.getId();
        result.title = video.getTitle();
        result.imageUrl = video.getImageUrl();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
        result.createdAt = video.getCreatedAt().format(formatter);
        result.views = video.getCountOfViews();
        Map<String, Integer> countOfLikesAndDislikes = video.getCountOfLikesAndDislikes();
        result.likes = countOfLikesAndDislikes.get("likes");
        result.dislikes = countOfLikesAndDislikes.get("dislikes");
        return result;
    }

    public static VideoSummaryDto fromWithIsLiked(Video video, User user) {
        VideoSummaryDto result = from(video);
        result.isLiked = video.isLiked(user, 1);
        result.isDisliked = video.isLiked(user, -1);
        return result;
    }
}
