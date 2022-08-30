package com.vid.scraper.controller;

import com.vid.scraper.model.CreateRecommendationViewRequest;
import com.vid.scraper.model.EmptyBodyResponse;
import com.vid.scraper.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/view")
public class ViewController {
    private final ViewService viewService;

    @PostMapping("/recommendation")
    public ResponseEntity<?> createRecommendationView(@RequestHeader("Authorization") String rawToken,
                                                      @RequestBody CreateRecommendationViewRequest request) {
        viewService.createRecommendationView(rawToken, request);
        return ResponseEntity.status(HttpStatus.OK).body(new EmptyBodyResponse());
    }
}
