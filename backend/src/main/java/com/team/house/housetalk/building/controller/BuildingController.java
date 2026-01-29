package com.team.house.housetalk.building.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.building.dto.BuildingCreateRequest;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * 로그인한 관리자가 관리하는 건물 목록 조회
     */
    @GetMapping
    public List<BuildingEntity> getMyBuildings(Authentication authentication) {
        // principal = adminId (Long)
        Long adminId = (Long) authentication.getPrincipal();

        Admin admin = buildingService.getAdminById(adminId);

        return buildingService.getBuildingsByAdmin(admin);
    }

    /**
     * 건물 생성
     */
    @PostMapping
    public BuildingEntity createBuilding(
            @RequestBody BuildingCreateRequest request,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        BuildingEntity building = BuildingEntity.create(
                admin,
                request.getName(),
                request.getAddress(),
                request.getTotalFloors(),
                request.getTotalUnits()
        );

        return buildingService.createBuilding(building);
    }

    /**
     * 건물 수정
     */
    @PutMapping("/{buildingId}")
    public BuildingEntity updateBuilding(
            @PathVariable Long buildingId,
            @RequestBody BuildingCreateRequest request,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        return buildingService.updateBuilding(
                buildingId,
                admin,
                request.getName(),
                request.getAddress(),
                request.getTotalFloors(),
                request.getTotalUnits()
        );
    }

    /**
     * 건물 삭제
     */
    @DeleteMapping("/{buildingId}")
    public void deleteBuilding(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        buildingService.deleteBuilding(buildingId, admin);
    }

    /**
     * 건물 단건 조회 (상세 페이지용)
     */
    @GetMapping("/{buildingId}")
    public BuildingEntity getBuilding(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        return buildingService.getBuildingById(buildingId, admin);
    }

}
