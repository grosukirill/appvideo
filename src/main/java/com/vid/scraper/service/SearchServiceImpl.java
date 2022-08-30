package com.vid.scraper.service;

import com.vid.scraper.exception.IllegalSearchTypeException;
import com.vid.scraper.model.*;
import com.vid.scraper.model.entity.Tag;
import com.vid.scraper.model.entity.Video;
import com.vid.scraper.repository.TagRepository;
import com.vid.scraper.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final VideoRepository videoRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Response elasticSearch(String search) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Video> foundVideos = videoRepository.findAllByTitleContainingIgnoreCase(search, pageable);
        List<Tag> foundTags = tagRepository.findAllByTagNameContainingIgnoreCase(search, pageable);
        List<ElasticSearchObject> elasticSearchObjects = new ArrayList<>();
        foundTags.forEach(tag -> elasticSearchObjects.add(new ElasticSearchObject(tag.getTagName(), SearchType.TAG)));
        foundVideos.getContent().forEach(video -> elasticSearchObjects.add(new ElasticSearchObject(video.getTitle(), SearchType.VIDEO)));
        return new Response(true, elasticSearchObjects, null);
    }

    @Override
    @Transactional
    public Response searchVideos(String search, String type, Integer page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Video> videos;
        switch (type) {
            case "tag":
                Tag tag = tagRepository.getByTagName(search);
                videos = videoRepository.findAllByTagsContaining(tag, pageable);
                break;
            case "video":
                videos = videoRepository.findAllByTitleContainingIgnoreCase(search, pageable);
                break;
            default:
                throw new IllegalSearchTypeException(String.format("Search type [%s] is illegal.", type));
        }
        List<VideoDto> videoDtos = new ArrayList<>();
        videos.forEach(video -> videoDtos.add(VideoDto.from(video)));
        return new Response(true, videoDtos, new PaginationDto(videos.hasNext(), page+1));
    }
}
