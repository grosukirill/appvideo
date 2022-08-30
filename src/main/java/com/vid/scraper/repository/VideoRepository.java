package com.vid.scraper.repository;

import com.vid.scraper.model.entity.Tag;
import com.vid.scraper.model.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    boolean existsByTitle(String title);

    @Query(nativeQuery = true, value = "select v.* from public.video as v join video_tags vt on v.id = vt.video_id where vt.tags_id =:tag_id offset :from fetch first 20 rows only;")
    List<Video> getAllVideoByTagId(@Param("tag_id") Long tagId, @Param("from") int from);

    Page<Video> findAllByTitleContainingIgnoreCase(String search, Pageable pageable);

    @Query(nativeQuery = true, value = "select v.*, count(views.id) as number_of_views from public.video as v left join views on v.id = views.video_id where views.created_at between current_timestamp - :days * interval '1' day and current_timestamp group by v.id order by number_of_views desc offset :from fetch first :perPage rows only;")
    List<Video> findAllByOrderByViewsDesc(@Param("from") int from, @Param("perPage") int perPage, @Param("days") int days);

    @Query(nativeQuery = true, value = "select v.* from likes as l join video as v on l.video_id = v.id join users as u on l.user_id = u.id where l.like_type=1 and l.user_id=:userId offset :offset fetch first 20 rows only;")
    List<Video> findVideosByLiked(@Param("userId") Long userId, @Param("offset") int offset);

    @Query(nativeQuery = true, value = "select * from public.video order by random() fetch first 20 rows only;")
    List<Video> findRandomVideos();

    @Query(nativeQuery = true, value = "select v.* from public.video as v where v.id in (select vt.video_id from public.video_tags as vt where vt.tags_id in (select vt2.tags_id from public.video_tags as vt2 where vt2.video_id =:videoId)) offset :offset fetch first :rows rows only;")
    List<Video> findSimilar(Long videoId, Integer offset, Integer rows);

    Page<Video> findAllByTagsContaining(Tag tag, Pageable pageable);
}
