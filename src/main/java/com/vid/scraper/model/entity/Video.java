package com.vid.scraper.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    @Lob
    private String fullHdUrl;
    @Lob
    private String hdUrl;
    @Lob
    private String sdUrl;
    @Lob
    private String imageUrl;
    @Lob
    private String description;
    @OneToMany(mappedBy = "video")
    private List<View> views = new ArrayList<>();
    @OneToMany(mappedBy = "video")
    private List<Like> likes = new ArrayList<>();
    @OneToMany(mappedBy = "video")
    private List<RecommendationView> recommendationViews = new ArrayList<>();
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToMany
    private List<Tag> tags;

    public Integer getCountOfViews() {
        return getViews().size();
    }

    public Map<String, Integer> getCountOfLikesAndDislikes() {
        Integer countOfLikes = 0;
        Integer countOfDislikes = 0;
        for (int i = 0; i < getLikes().size(); i++) {
            if (getLikes().get(i).getLikeType() == 1) {
                countOfLikes++;
            } else {
                countOfDislikes++;
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("likes", countOfLikes);
        result.put("dislikes", countOfDislikes);
        return result;
    }

    public Boolean isLiked(User user, int type) {
        return this.getLikes().stream().anyMatch(like -> (like.getUser().equals(user) && like.getLikeType() == type));
    }

    public Video removeLike(User user, int type) {
        this.getLikes().removeIf(like -> like.getUser().equals(user) && like.getLikeType() == type);
        return this;
    }
}
