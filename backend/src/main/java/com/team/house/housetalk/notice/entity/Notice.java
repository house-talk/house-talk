package com.team.house.housetalk.notice.entity;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.building.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 건물의 공지인가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingEntity building;

    // 작성한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String writer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ 핵심 수정 포인트
    @OneToMany(
            mappedBy = "notice",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("orderIndex ASC")
    private List<NoticeImage> images;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Notice(
            BuildingEntity building,
            Admin admin,
            String title,
            String content,
            String writer
    ) {
        this.building = building;
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

