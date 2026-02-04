package com.team.house.housetalk.tenant.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.tenant.dto.TenantApprovalResponse;
import com.team.house.housetalk.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tenants")
public class AdminTenantController {

    private final TenantService tenantService;
    private final AdminRepository adminRepository;

    /**
     * 🔐 인증 정보에서 adminId 안전하게 추출
     * (JWT + Google OAuth 모두 지원)
     */
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

            if (providerId == null) {
                throw new IllegalStateException("OAuth 사용자 식별자(sub)가 없습니다.");
            }

            return adminRepository
                    .findByProviderAndProviderUserId("google", providerId)
                    .map(Admin::getId)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("지원하지 않는 인증 방식입니다.");
    }

    /**
     * 관리자 승인 대기 목록 조회
     */
    @GetMapping("/pending")
    public List<TenantApprovalResponse> getPendingTenants(
            @RequestParam Long buildingId,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);

        return tenantService.getPendingRequests(buildingId, adminId)
                .stream()
                .map(TenantApprovalResponse::new)
                .toList();
    }

    /**
     * 승인
     */
    @PostMapping("/{tenantBuildingId}/approve")
    public void approveTenant(
            @PathVariable Long tenantBuildingId,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);
        tenantService.approveTenant(tenantBuildingId, adminId);
    }

    /**
     * 거절
     */
    @DeleteMapping("/{tenantBuildingId}")
    public void rejectTenant(
            @PathVariable Long tenantBuildingId,
            Authentication authentication
    ) {
        Long adminId = getAdminId(authentication);
        tenantService.rejectTenant(tenantBuildingId, adminId);
    }
}
