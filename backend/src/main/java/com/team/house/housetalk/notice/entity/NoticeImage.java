package com.team.house.housetalk.notice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notice_image")
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 공지의 이미지인가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String originalName; // ⭐ 원본 파일명
    // 이미지 순서
    private int orderIndex;

    @Builder
    public NoticeImage(Notice notice, String imageUrl, int orderIndex,String originalName) {
        this.notice = notice;
        this.imageUrl = imageUrl;
        this.orderIndex = orderIndex;
        this.originalName = originalName;

    }
}
