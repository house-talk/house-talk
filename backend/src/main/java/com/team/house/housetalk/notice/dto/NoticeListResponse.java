package com.team.house.housetalk.notice.dto;

import com.team.house.housetalk.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeListResponse {

    private Long id;
    private String title;
    private String writer;
    private LocalDateTime createdAt;

    public static NoticeListResponse from(Notice notice) {
        return new NoticeListResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getWriter(),
                notice.getCreatedAt()
        );
    }
}
