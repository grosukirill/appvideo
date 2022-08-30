package com.vid.scraper.controller;

import com.vid.scraper.model.Response;
import com.vid.scraper.model.VideoDto;
import com.vid.scraper.model.VideoSummaryDto;
import com.vid.scraper.service.VideoService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;

    @GetMapping
    public ResponseEntity<?> getVideos(@RequestParam("page") Integer page) {
        Response response = videoService.getVideos(page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideo(@PathVariable("id") Long videoId, @Nullable @RequestHeader("Authorization") String rawToken) {
        Response response = videoService.getVideoById(videoId, rawToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularVideos(@RequestParam("page") Integer page, @RequestParam("perPage") Integer perPage, @RequestParam("days") Integer days) {
        Response response = videoService.getPopularVideos(page, perPage, days);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@Nullable @RequestHeader("Authorization") String rawToken,
                                                @RequestParam("page") Integer page,
                                                @Nullable @CookieValue("ids") String videoIds) {
        Response response = videoService.getRecommendations(rawToken, page, videoIds);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/similar")
    public ResponseEntity<?> getSimilar(@RequestParam("id") Long videoId, @RequestParam("page") Integer page) {
        Response response = videoService.getSimilar(videoId, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
