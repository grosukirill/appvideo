package com.vid.scraper.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRecommendationViewRequest {
    private Long videoId;

    @JsonCreator
    public CreateRecommendationViewRequest(@JsonProperty("videoId") Long videoId) {
        this.videoId = videoId;
    }
}
