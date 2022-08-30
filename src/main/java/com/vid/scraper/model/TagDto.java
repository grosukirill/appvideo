package com.vid.scraper.model;

import com.vid.scraper.model.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String tagName;
    private Integer countOfVideos;

    public static TagDto from(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.id = tag.getId();
        tagDto.tagName = tag.getTagName();
        return tagDto;
    }

    public static TagDto from(Tag tag, Integer count) {
        TagDto tagDto = new TagDto();
        tagDto.id = tag.getId();
        tagDto.tagName = tag.getTagName();
        tagDto.countOfVideos = count;
        return tagDto;
    }
}
