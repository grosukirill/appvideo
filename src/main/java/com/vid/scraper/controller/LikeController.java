package com.vid.scraper.controller;

import com.vid.scraper.model.CreateLikeRequest;
import com.vid.scraper.model.Response;
import com.vid.scraper.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<?> like(@RequestHeader("Authorization") String rawToken, @RequestBody CreateLikeRequest request) {
        Response response = likeService.like(rawToken, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/video")
    public ResponseEntity<?> findLikedVideos(@RequestHeader("Authorization") String rawToken, @RequestParam("page") Integer page) {
        Response response = likeService.findLikeVideos(rawToken, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
