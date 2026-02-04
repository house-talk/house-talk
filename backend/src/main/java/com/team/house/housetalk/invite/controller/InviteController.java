package com.team.house.housetalk.invite.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.service.BuildingService;
import com.team.house.housetalk.invite.dto.InviteCreateResponse;
import com.team.house.housetalk.invite.dto.InviteFindResponse;
import com.team.house.housetalk.invite.dto.InviteValidateRequest;
import com.team.house.housetalk.invite.dto.InviteValidateResponse;
import com.team.house.housetalk.invite.entity.Invite;
import com.team.house.housetalk.invite.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;
    private final BuildingService buildingService;
    private final AdminRepository adminRepository; // ✅ 추가

    /**
     * 🔐 인증 정보에서 Admin 안전하게 추출
     */
    private Admin getAuthenticatedAdmin(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        // 1️⃣ JWT 로그인
        if (principal instanceof Long adminId) {
            return buildingService.getAdminById(adminId);
        }

        // 2️⃣ Google OAuth 로그인
        if (principal instanceof OAuth2User oauthUser) {
            Map<String, Object> attributes = oauthUser.getAttributes();
            String providerId = (String) attributes.get("sub");

            return adminRepository
                    .findByProviderAndProviderUserId("google", providerId)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("알 수 없는 인증 방식입니다.");
    }

    /**
     * 관리자용 초대코드 발급
     * POST /api/admin/invites?buildingId=1
     */
    @PostMapping("/api/admin/invites")
    public InviteCreateResponse createInvite(
            @RequestParam Long buildingId,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);

        // 관리자 소유 건물 검증
        BuildingEntity building = buildingService.getBuildingById(buildingId, admin);

        Invite invite = inviteService.createInvite(building);

        return new InviteCreateResponse(invite.getInviteCode());
    }

    /**
     * 관리자용 초대코드 조회
     * GET /api/admin/invites?buildingId=1
     */
    @GetMapping("/api/admin/invites")
    public InviteFindResponse findInvite(
            @RequestParam Long buildingId,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);

        // 관리자 소유 건물 검증
        BuildingEntity building = buildingService.getBuildingById(buildingId, admin);

        Invite invite = inviteService.findByBuilding(building);

        if (invite == null) {
            return new InviteFindResponse(null);
        }

        return new InviteFindResponse(invite.getInviteCode());
    }

    /**
     * 세입자용 초대코드 검증
     * POST /api/tenant/invites/validate
     */
    @PostMapping("/api/tenant/invites/validate")
    public InviteValidateResponse validateInvite(
            @RequestBody InviteValidateRequest request
    ) {
        Invite invite = inviteService.validateInvite(request.getInviteCode());
        BuildingEntity building = invite.getBuilding();

        return new InviteValidateResponse(
                building.getId(),
                building.getName()
        );
    }
}
