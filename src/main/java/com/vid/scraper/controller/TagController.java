package com.vid.scraper.controller;

import com.vid.scraper.model.*;
import com.vid.scraper.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    @GetMapping("/popular")
    public ResponseEntity<?> findPopularTags() {
        Response response = tagService.findPopular();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<?> findAllTags(@RequestParam("page") Integer pageNumber) {
        Response allTags = tagService.findAll(pageNumber);
        return ResponseEntity.status(HttpStatus.OK).body(allTags);
    }

    @GetMapping("/videos")
    public ResponseEntity<?> findAllVideosByTag(@RequestParam("tag") Long tagId, @RequestParam("page") Integer page) {
        VideoByTagResponse response = tagService.findAllVideosByTag(tagId, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
