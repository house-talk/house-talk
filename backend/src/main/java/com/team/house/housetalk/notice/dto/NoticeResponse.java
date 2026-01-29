package com.team.house.housetalk.notice.dto;

import com.team.house.housetalk.notice.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NoticeResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdAt;

    private List<FileResponse> files; // ⭐ 변경

    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getWriter(),
                notice.getCreatedAt(),
                notice.getImages().stream()
                        .map(img -> new FileResponse(
                                img.getId(),
                                img.getImageUrl(),
                                img.getOriginalName()
                        ))
                        .toList()
        );
    }

    private NoticeResponse(
            Long id,
            String title,
            String content,
            String writer,
            LocalDateTime createdAt,
            List<FileResponse> files
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
        this.files = files;
    }
}

