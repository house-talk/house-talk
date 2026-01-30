package com.team.house.housetalk.notice.controller;

import com.team.house.housetalk.notice.dto.NoticeListResponse;
import com.team.house.housetalk.notice.dto.NoticeResponse;
import com.team.house.housetalk.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /* =========================
       공지 목록 조회 (세입자 / 공용)
    ========================= */
    @GetMapping
    public List<NoticeResponse> getNotices(
            @PathVariable Long buildingId
    ) {
        return noticeService.getNotices(buildingId);
    }

    /* =========================
       ⭐ 공지 검색 + 페이징 조회
       - keyword 기준
       - 제목 + 내용 통합 검색
    ========================= */
    @GetMapping("/search")
    public Page<NoticeListResponse> searchNotices(
            @PathVariable Long buildingId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return noticeService.searchNotices(buildingId, keyword, page, size);
    }

    /* =========================
       공지 단건 조회 (세입자 / 공용)
    ========================= */
    @GetMapping("/{noticeId}")
    public NoticeResponse getNotice(
            @PathVariable Long buildingId,
            @PathVariable Long noticeId
    ) {
        return noticeService.getNotice(buildingId, noticeId);
    }
}
