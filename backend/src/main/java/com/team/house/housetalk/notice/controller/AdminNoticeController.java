package com.team.house.housetalk.notice.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.notice.dto.NoticeCreateRequest;
import com.team.house.housetalk.notice.dto.NoticeUpdateRequest;
import com.team.house.housetalk.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/buildings/{buildingId}/notices")
public class AdminNoticeController {

    private final NoticeService noticeService;
    private final AdminRepository adminRepository; // ✅ 추가

    /* =========================
       공지 생성 (관리자만)
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
       관리자 인증 공통 처리 (JWT + OAuth)
    ========================= */
    private Long getAdminId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        // 1️⃣ JWT 로그인
        if (principal instanceof Long adminId) {
            return adminId;
        }

        // 2️⃣ Google OAuth 로그인
        if (principal instanceof OAuth2User oauthUser) {
            Map<String, Object> attributes = oauthUser.getAttributes();
            String providerId = (String) attributes.get("sub");

            return adminRepository
                    .findByProviderAndProviderUserId("google", providerId)
                    .map(Admin::getId)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("지원하지 않는 인증 방식입니다.");
    }
}
