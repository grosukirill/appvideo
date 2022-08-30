package com.vid.scraper.model;

public enum ErrorCode {
    AUTHENTICATION_ERROR(1),
    BAD_CREDENTIALS(2),
    EMAIL_TAKEN(3),
    TAG_NOT_FOUND(4),
    VIDEO_NOT_FOUND(5),
    INVALID_TOKEN(6),
    MATCHING_PASSWORDS(7),
    DUPLICATE_VIEW(8),
    ILLEGAL_SEARCH_TYPE(9);


    public final Integer number;

    ErrorCode(Integer number) {
        this.number = number;
    }
}
