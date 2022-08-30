package com.vid.scraper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateLikeRequest {
    private Long videoId;
    private Integer like;

    @JsonCreator
    public CreateLikeRequest(@JsonProperty("videoId") Long videoId,
                             @JsonProperty("like") Integer like) {
        this.videoId = videoId;
        this.like = like;
    }
}
