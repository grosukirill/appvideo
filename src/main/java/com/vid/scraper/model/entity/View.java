package com.vid.scraper.model.entity;

import com.vid.scraper.model.entity.Video;
import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "views")
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private OffsetDateTime createdAt;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "video_id", nullable = false, updatable = false)
    private Video video;
}
