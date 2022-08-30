package com.vid.scraper.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "recommendation_views")
public class RecommendationView {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private OffsetDateTime createdAt;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "video_id", nullable = false, updatable = false)
    private Video video;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Override
    public String toString() {
        return user.getId() + "," + video.getId() + ",1";
    }

}
