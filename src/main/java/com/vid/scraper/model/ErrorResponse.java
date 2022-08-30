package com.vid.scraper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private final Boolean status = false;
    private ErrorDto error;

    public ErrorResponse(ErrorDto error) {
        this.error = error;
    }
}
