package com.team.house.housetalk.building.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository; // 레포지토리 추가
import com.team.house.housetalk.building.dto.BuildingCreateRequest;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.service.BuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final AdminRepository adminRepository; // Admin 정보를 직접 찾기 위해 추가

    /**
     * ✅ 안전하게 Admin ID를 추출하는 헬퍼 메서드
     * (JWT와 세션 로그인 모두 대응)
     */
    private Admin getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        Long adminId = null;

        // 1. JWT (Long 타입일 때)
        if (principal instanceof Long id) {
            adminId = id;
        }
        // 2. 구글 로그인 세션 (OAuth2User 타입일 때)
        else if (principal instanceof OAuth2User oauthUser) {
            Map<String, Object> attributes = oauthUser.getAttributes();
            String providerId = (String) attributes.get("sub");
            // 구글 ID로 Admin 찾기
            return adminRepository.findByProviderAndProviderUserId("google", providerId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
        }

        if (adminId != null) {
            return buildingService.getAdminById(adminId);
        }

        throw new IllegalStateException("인증 타입이 올바르지 않습니다: " + principal.getClass().getName());
    }

    /**
     * 로그인한 관리자가 관리하는 건물 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<BuildingResponse>> getMyBuildings(Authentication authentication) {
        Admin admin = getAuthenticatedAdmin(authentication);
        List<BuildingEntity> buildings = buildingService.getBuildingsByAdmin(admin);

        // 엔티티 -> DTO 변환 후 반환
        List<BuildingResponse> response = buildings.stream()
                .map(BuildingResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 건물 생성
     */
    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(
            @RequestBody BuildingCreateRequest request,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);

        BuildingEntity building = BuildingEntity.create(
                admin,
                request.getName(),
                request.getAddress(),
                request.getTotalFloors(),
                request.getTotalUnits()
        );

        BuildingEntity savedBuilding = buildingService.createBuilding(building);
        return ResponseEntity.ok(BuildingResponse.from(savedBuilding));
    }

    /**
     * 건물 수정
     */
    @PutMapping("/{buildingId}")
    public ResponseEntity<BuildingResponse> updateBuilding(
            @PathVariable Long buildingId,
            @RequestBody BuildingCreateRequest request,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);

        BuildingEntity updatedBuilding = buildingService.updateBuilding(
                buildingId,
                admin,
                request.getName(),
                request.getAddress(),
                request.getTotalFloors(),
                request.getTotalUnits()
        );
        return ResponseEntity.ok(BuildingResponse.from(updatedBuilding));
    }

    /**
     * 건물 삭제
     */
    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);
        buildingService.deleteBuilding(buildingId, admin);
        return ResponseEntity.ok().build();
    }

    /**
     * 건물 단건 조회
     */
    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingResponse> getBuilding(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        Admin admin = getAuthenticatedAdmin(authentication);
        BuildingEntity building = buildingService.getBuildingById(buildingId, admin);
        return ResponseEntity.ok(BuildingResponse.from(building));
    }

    /**
     * ✅ 응답용 DTO (Inner Record)
     * - 엔티티를 직접 반환하지 않기 위해 사용
     */
    public record BuildingResponse(
            Long id,
            String name,
            String address,
            Integer totalFloors,
            Integer totalUnits
    ) {
        public static BuildingResponse from(BuildingEntity entity) {
            return new BuildingResponse(
                    entity.getId(),
                    entity.getName(),
                    entity.getAddress(),
                    entity.getTotalFloors(),
                    entity.getTotalUnits()
            );
        }
    }
}
