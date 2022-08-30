package com.vid.scraper.service;

import com.vid.scraper.exception.TagNotFoundException;
import com.vid.scraper.model.*;
import com.vid.scraper.model.entity.Tag;
import com.vid.scraper.model.entity.Video;
import com.vid.scraper.repository.TagRepository;
import com.vid.scraper.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final VideoRepository videoRepository;

    @Override
    public Response findPopular() {
        List<Tag> popular = tagRepository.findPopular();
        List<TagDto> dtos = new ArrayList<>();
        popular.forEach(tag -> {
            Integer countOfVidoes = tagRepository.getCountOfVideos(tag.getId());
            dtos.add(TagDto.from(tag, countOfVidoes));
        });
        return new Response(true, dtos, null);
    }

    @Override
    public Response findAll(Integer pageNumber) {
        int from = pageNumber * 200;
        List<Tag> tags = tagRepository.findAllPopularFirst(from);
        List<TagDto> tagDtos = new ArrayList<>();
        for (Tag tag: tags) {
            Integer count = tagRepository.getCountOfVideos(tag.getId());
            tagDtos.add(TagDto.from(tag, count));
        }
        if (tags.size() < 200) {
            return new Response(true, tagDtos, new PaginationDto(false, pageNumber));
        } else {
            return new Response(true, tagDtos, new PaginationDto(true, pageNumber+1));
        }
    }

    @Override
    @Transactional
    public VideoByTagResponse findAllVideosByTag(Long tagId, Integer page) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (!tag.isPresent()) {
            throw new TagNotFoundException(String.format("Tag with ID [%s] not found!", tagId));
        }
        int from = page * 20;
        List<Video> videos = videoRepository.getAllVideoByTagId(tag.get().getId(), from);
        List<VideoSummaryDto> videoDtos = new ArrayList<>();
        videos.forEach(video -> videoDtos.add(VideoSummaryDto.from(video)));
        PaginationDto paginationDto = new PaginationDto(true, page+1);
        if (videos.size() < 20) {
            paginationDto.setHasNextPage(false);
            paginationDto.setNextPage(page);
        }
        return new VideoByTagResponse(true, tag.get().getTagName(), videoDtos, paginationDto);
    }
}
