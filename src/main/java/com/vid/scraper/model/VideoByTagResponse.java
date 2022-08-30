package com.vid.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoByTagResponse {
    private Boolean status;
    private String tag;
    private Object data;
    private PaginationDto pagination;
}
