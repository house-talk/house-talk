package com.team.house.housetalk.notice.controller;

import com.team.house.housetalk.notice.dto.NoticeCreateRequest;
import com.team.house.housetalk.notice.dto.NoticeUpdateRequest;
import com.team.house.housetalk.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/buildings/{buildingId}/notices")
public class AdminNoticeController {

    private final NoticeService noticeService;

    /* =========================
       공지 생성 (관리자만)
       ✅ multipart + ModelAttribute
    ========================= */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createNotice(
            @PathVariable Long buildingId,
            @ModelAttribute NoticeCreateRequest request,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);
        return noticeService.createNotice(buildingId, adminId, request);
    }

    /* =========================
       공지 수정 (관리자만)
       ✅ 생성과 동일한 ModelAttribute 방식
    ========================= */
    @PatchMapping(
            value = "/{noticeId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void updateNotice(
            @PathVariable Long buildingId,
            @PathVariable Long noticeId,
            @ModelAttribute NoticeUpdateRequest request,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);
        noticeService.updateNotice(buildingId, noticeId, adminId, request);
    }

    /* =========================
       공지 삭제 (관리자만)
    ========================= */
    @DeleteMapping("/{noticeId}")
    public void deleteNotice(
            @PathVariable Long buildingId,
            @PathVariable Long noticeId,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);
        noticeService.deleteNotice(buildingId, noticeId, adminId);
    }

    /* =========================
       관리자 인증 공통 검증
    ========================= */
    private Long getAdminId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new IllegalStateException("관리자만 접근할 수 있습니다.");
        }
        return (Long) authentication.getPrincipal();
    }
}
