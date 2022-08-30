package com.vid.scraper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaginationDto {
    private Boolean hasNextPage;
    private Integer nextPage;

    public PaginationDto(Boolean hasNextPage, Integer nextPage) {
        this.hasNextPage = hasNextPage;
        if (hasNextPage) {
            this.nextPage = nextPage;
        }
    }
}
