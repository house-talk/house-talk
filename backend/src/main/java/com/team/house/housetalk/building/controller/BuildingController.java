package com.team.house.housetalk.building.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
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
    private final AdminRepository adminRepository;

    // ✅ 안전한 인증 정보 추출 메서드 (필수!)
    private Admin getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        Object principal = authentication.getPrincipal();

        // JWT (Long)
        if (principal instanceof Long) {
            return buildingService.getAdminById((Long) principal);
        }
        // OAuth2 (Google User)
        if (principal instanceof OAuth2User) {
            String googleId = (String) ((OAuth2User) principal).getAttributes().get("sub");
            return adminRepository.findByProviderAndProviderUserId("google", googleId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
        }
        throw new IllegalStateException("인증 타입 오류");
    }

    /**
     * 내 건물 목록 조회 (DTO 변환 적용!)
     */
    @GetMapping
    public ResponseEntity<List<BuildingResponse>> getMyBuildings(Authentication authentication) {
        Admin admin = getAuthenticatedAdmin(authentication);
        List<BuildingEntity> buildings = buildingService.getBuildingsByAdmin(admin);

        // 엔티티 리스트를 DTO 리스트로 변환 (핵심!)
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
        BuildingEntity saved = buildingService.createBuilding(building);
        return ResponseEntity.ok(BuildingResponse.from(saved));
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
        BuildingEntity updated = buildingService.updateBuilding(
                buildingId, admin, request.getName(), request.getAddress(),
                request.getTotalFloors(), request.getTotalUnits()
        );
        return ResponseEntity.ok(BuildingResponse.from(updated));
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

    // ✅ 응답용 DTO (Inner Record)
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
