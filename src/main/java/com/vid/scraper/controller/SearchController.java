package com.vid.scraper.controller;

import com.vid.scraper.model.ElasticSearchObject;
import com.vid.scraper.model.Response;
import com.vid.scraper.model.VideoDto;
import com.vid.scraper.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> elasticSearch(@RequestParam("search") String search) {
        Response response = searchService.elasticSearch(search);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/video")
    public ResponseEntity<?> searchVideos(@RequestParam("search") String search, @RequestParam("type") String type, @RequestParam("page") Integer page) {
        Response response = searchService.searchVideos(search, type, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
