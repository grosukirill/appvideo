package com.vid.scraper.repository;

import com.vid.scraper.model.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByTagName(String tagName);

    @Query(nativeQuery = true, value = "select t.*, COUNT(vt.tags_id) as occurences from public.tags as t, public.video_tags as vt where t.id = vt.tags_id group by t.id order by occurences desc fetch first 10 rows only")
    List<Tag> findPopular();

    Tag getByTagName(String tagName);

    @Query(nativeQuery = true, value = "select count(video_id) from public.video_tags where tags_id =:tag_id")
    Integer getCountOfVideos(@Param("tag_id") Long tagId);

    @Query(nativeQuery = true, value = "select t.*, count(vt.video_id) from public.tags as t join video_tags as vt on t.id = vt.tags_id group by t.id order by count(vt.video_id) desc offset :from fetch first 200 rows only")
    List<Tag> findAllPopularFirst(@Param("from") int from);

    List<Tag> findAllByTagNameContainingIgnoreCase(String search, Pageable pageable);
}
