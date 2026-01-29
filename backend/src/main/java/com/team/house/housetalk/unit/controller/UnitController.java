package com.team.house.housetalk.unit.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.building.service.BuildingService;
import com.team.house.housetalk.unit.dto.*;
import com.team.house.housetalk.unit.entity.Unit;
import com.team.house.housetalk.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/units")
public class UnitController {

    private final UnitService unitService;
    private final BuildingService buildingService;

    /**
     * 특정 건물의 세대 목록 조회
     */
    /**
     * ⭐ 세대 상태 조회 (거주 여부 + 세입자 정보 포함)
     */
    @GetMapping("/status")
    public List<UnitStatusResponse> getUnitStatuses(
            @PathVariable Long buildingId,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        // 🔥 핵심: UnitService의 새 메서드 사용
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
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

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
     * ⭐ 여러 세대 한 번에 생성
     */
    @PostMapping("/bulk")
    public void createUnitsBulk(
            @PathVariable Long buildingId,
            @RequestBody UnitBulkCreateRequest request,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

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
     * 세대 수정 (부분 수정)
     */
    @PatchMapping("/{unitId}")
    public UnitResponse updateUnit(
            @PathVariable Long buildingId,
            @PathVariable Long unitId,
            @RequestBody UnitUpdateRequest request,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

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
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        unitService.deleteUnit(unitId, admin);
    }

    /**
     * ⭐ 세대 순서(orderIndex) 변경
     */
    @PatchMapping("/order")
    public void updateUnitOrder(
            @PathVariable Long buildingId,
            @RequestBody UnitOrderUpdateRequest request,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        Admin admin = buildingService.getAdminById(adminId);

        unitService.updateUnitOrder(
                buildingId,
                admin,
                request.getOrders()
        );
    }

}
