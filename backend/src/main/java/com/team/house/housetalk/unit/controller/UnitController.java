package com.team.house.housetalk.unit.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository; // ✅ 추가됨
import com.team.house.housetalk.building.service.BuildingService;
import com.team.house.housetalk.unit.dto.*;
import com.team.house.housetalk.unit.entity.Unit;
import com.team.house.housetalk.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User; // ✅ 추가됨
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/units")
public class UnitController {

    private final UnitService unitService;
    private final BuildingService buildingService;
    private final AdminRepository adminRepository; // ✅ DB 조회를 위해 추가

    /**
     * 🛡️ 인증 정보에서 안전하게 Admin 객체를 꺼내는 메서드
     * (JWT 숫자 ID와 구글 로그인 객체 모두 처리)
     */
    private Admin getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        // 1. JWT 로그인 (Long ID인 경우)
        if (principal instanceof Long adminId) {
            return buildingService.getAdminById(adminId);
        }

        // 2. 구글 로그인 (OAuth2User 객체인 경우)
        else if (principal instanceof OAuth2User oauthUser) {
            Map<String, Object> attributes = oauthUser.getAttributes();
            String providerId = (String) attributes.get("sub"); // 구글의 고유 ID

            return adminRepository.findByProviderAndProviderUserId("google", providerId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
        }

        throw new IllegalStateException("지원하지 않는 인증 방식입니다.");
    }

    /**
     * 세대 상태 조회
     */
    @GetMapping("/status")
    public List<UnitStatusResponse> getUnitStatuses(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        // ✅ 수정됨: 안전하게 Admin 가져오기
        Admin admin = getAuthenticatedAdmin(authentication);
        return unitService.getUnitStatuses(buildingId, admin);
    }

    /**
     * 세대 생성
     */
    @PostMapping
    public UnitResponse createUnit(
            @PathVariable Long buildingId,
            @RequestBody UnitCreateRequest request,
            Authentication authentication
    ) {
        // ✅ 수정됨
        Admin admin = getAuthenticatedAdmin(authentication);

        Unit unit = unitService.createUnit(
                buildingId,
                admin,
                request.getFloor(),
                request.getUnitNumber(),
                request.getIsOccupied(),
                request.getMemo()
        );

        return new UnitResponse(unit);
    }

    /**
     * 여러 세대 한 번에 생성
     */
    @PostMapping("/bulk")
    public void createUnitsBulk(
            @PathVariable Long buildingId,
            @RequestBody UnitBulkCreateRequest request,
            Authentication authentication
    ) {
        // ✅ 수정됨
        Admin admin = getAuthenticatedAdmin(authentication);

        unitService.createUnitsBulk(
                buildingId,
                admin,
                request.getFloor(),
                request.getStartUnit(),
                request.getEndUnit(),
                request.getIsOccupied(),
                request.getMemo()
        );
    }

    /**
     * 세대 수정
     */
    @PatchMapping("/{unitId}")
    public UnitResponse updateUnit(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @RequestBody UnitUpdateRequest request,
            Authentication authentication
    ) {
        // ✅ 수정됨
        Admin admin = getAuthenticatedAdmin(authentication);

        Unit unit = unitService.updateUnit(
                unitId,
                admin,
                request.getFloor(),
                request.getUnitNumber(),
                request.getIsOccupied(),
                request.getMemo()
        );

        return new UnitResponse(unit);
    }

    /**
     * 세대 삭제
     */
    @DeleteMapping("/{unitId}")
    public void deleteUnit(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            Authentication authentication
    ) {
        // ✅ 수정됨
        Admin admin = getAuthenticatedAdmin(authentication);
        unitService.deleteUnit(unitId, admin);
    }

    /**
     * 세대 순서 변경
     */
    @PatchMapping("/order")
    public void updateUnitOrder(
            @PathVariable Long buildingId,
            @RequestBody UnitOrderUpdateRequest request,
            Authentication authentication
    ) {
        // ✅ 수정됨
        Admin admin = getAuthenticatedAdmin(authentication);

        unitService.updateUnitOrder(
                buildingId,
                admin,
                request.getOrders()
        );
    }
}
