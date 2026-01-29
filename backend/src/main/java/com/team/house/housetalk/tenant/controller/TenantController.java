package com.team.house.housetalk.tenant.controller;

import com.team.house.housetalk.tenant.dto.*;
import com.team.house.housetalk.tenant.entity.Tenant;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.tenant.repository.TenantRepository;
import com.team.house.housetalk.tenant.service.TenantService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/tenant")
public class TenantController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    /* ==================================================
       ⭐ 0️⃣ 세입자 인증 (기존 / 신규 공통)
    ================================================== */
    @PostMapping("/auth")
    public void authenticate(
            @RequestBody TenantAuthRequest request,
            HttpServletResponse response
    ) {
        Tenant tenant = tenantService.authenticateOrCreate(
                request.getPhoneNumber(),
                request.getPassword(),
                request.getName(),
                request.isNewUser()
        );

        Cookie cookie = new Cookie("tenantCode", tenant.getTenantCode());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);         // 로컬에서는 false
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
        response.addCookie(cookie);
    }

    /**
     * 세입자 집 추가 요청 (초대코드 기반)
     */
    @PostMapping("/join")
    public void joinBuilding(
            @RequestBody TenantJoinRequest request,
            @CookieValue(value = "tenantCode", required = false) String tenantCode
    ) {
        if (tenantCode == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        tenantService.requestJoinBuilding(
                request.getInviteCode(),
                request.getName(),
                request.getPhoneNumber(),
                request.getUnitNumber(),
                tenantCode
        );
    }

    /**
     * 세입자 홈: 내가 승인된 집 목록
     */
    @GetMapping("/homes")
    public List<TenantHomeResponse> getMyHomes(
            @CookieValue(value = "tenantCode", required = false) String tenantCode
    ) {
        if (tenantCode == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Tenant tenant = tenantRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new IllegalStateException("로그인이 필요합니다."));

        return tenant.getTenantBuildings().stream()
                .filter(TenantBuilding::isApproved)
                .map(TenantHomeResponse::new)
                .toList();
    }

    /**
     * 세입자 정보 조회 (환영 문구용)
     */
    @GetMapping("/me")
    public TenantMeResponse me(
            @CookieValue(value = "tenantCode", required = false) String tenantCode
    ) {
        if (tenantCode == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Tenant tenant = tenantRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new IllegalStateException("로그인이 필요합니다."));

        return new TenantMeResponse(tenant);
    }

    /**
     * 세입자 로그아웃
     */
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("tenantCode", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(0); // 즉시 삭제
        response.addCookie(cookie);
    }

    /* ==================================================
   6️⃣ 세입자 전용 건물 상세 조회
   - tenantBuildingId 기준
   - 본인 소유 + 승인된 건물만 접근 가능
================================================== */
    @GetMapping("/buildings/{tenantBuildingId}")
    public TenantBuildingDetailResponse getMyBuildingDetail(
            @PathVariable Long tenantBuildingId,
            @CookieValue(value = "tenantCode", required = false) String tenantCode
    ) {
        if (tenantCode == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        TenantBuilding tenantBuilding =
                tenantService.getApprovedTenantBuilding(tenantBuildingId, tenantCode);

        return TenantBuildingDetailResponse.from(tenantBuilding);
    }



}
