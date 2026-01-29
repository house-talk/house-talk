package com.team.house.housetalk.unit.service;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import com.team.house.housetalk.building.service.BuildingService;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.tenant.repository.TenantBuildingRepository;
import com.team.house.housetalk.unit.dto.UnitOrderUpdateRequest;
import com.team.house.housetalk.unit.dto.UnitStatusResponse;
import com.team.house.housetalk.unit.entity.Unit;
import com.team.house.housetalk.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final BuildingService buildingService;
    private final TenantBuildingRepository tenantBuildingRepository;
    private final BuildingRepository buildingRepository;
    /**
     * 특정 건물의 세대 목록 조회
     */
    public List<Unit> getUnitsByBuilding(Long buildingId, Admin admin) {
        BuildingEntity building = getAuthorizedBuilding(buildingId, admin);
        return unitRepository.findByBuildingOrderByFloorDescOrderIndexAsc(building);
    }

    /**
     * 세대 생성
     */
    public Unit createUnit(
            Long buildingId,
            Admin admin,
            Integer floor,
            String unitNumber,
            Boolean isOccupied,
            String memo
    ) {
        BuildingEntity building = getAuthorizedBuilding(buildingId, admin);

        // 호수 중복 체크
        unitRepository.findByBuildingAndUnitNumber(building, unitNumber)
                .ifPresent(unit -> {
                    throw new IllegalArgumentException("이미 존재하는 호수입니다");
                });

        int nextOrderIndex = unitRepository.countByBuildingAndFloor(building, floor) + 1;

        Unit unit = Unit.create(
                building,
                floor,
                unitNumber,
                nextOrderIndex,
                isOccupied,
                memo
        );

        return unitRepository.save(unit);
    }

    @Transactional
    public void createUnitsBulk(
            Long buildingId,
            Admin admin,
            Integer floor,
            Integer startUnit,
            Integer endUnit,
            Boolean isOccupied,
            String memo
    ) {
        BuildingEntity building = getAuthorizedBuilding(buildingId, admin);

        if (startUnit > endUnit) {
            throw new IllegalArgumentException("시작 호수는 끝 호수보다 작아야 합니다.");
        }

        // ⭐ 이 층의 마지막 orderIndex
        int baseOrderIndex =
                unitRepository.countByBuildingAndFloor(building, floor);

        int currentOrderIndex = baseOrderIndex + 1;

        for (int unitNum = startUnit; unitNum <= endUnit; unitNum++) {
            String unitNumber = String.valueOf(unitNum);

            // 중복 체크
            unitRepository.findByBuildingAndUnitNumber(building, unitNumber)
                    .ifPresent(u -> {
                        throw new IllegalArgumentException(
                                "이미 존재하는 호수입니다: " + unitNumber
                        );
                    });

            Unit unit = Unit.create(
                    building,
                    floor,
                    unitNumber,
                    currentOrderIndex,   // ⭐ 순서대로
                    isOccupied != null ? isOccupied : false,
                    memo
            );

            unitRepository.save(unit);
            currentOrderIndex++; // ⭐ 다음 순서
        }
    }


    /**
     * 세대 수정
     */
    public Unit updateUnit(
            Long unitId,
            Admin admin,
            Integer floor,
            String unitNumber,
            Boolean isOccupied,
            String memo
    ) {
        Unit unit = getAuthorizedUnit(unitId, admin);
        BuildingEntity building = unit.getBuilding();

        // 호수 변경 시 중복 체크
        if (unitNumber != null && !unitNumber.equals(unit.getUnitNumber())) {
            unitRepository.findByBuildingAndUnitNumber(building, unitNumber)
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("이미 존재하는 호수입니다");
                    });
        }

        // 부분 수정
        if (floor != null) {
            unit.updateFloor(floor);
        }
        if (unitNumber != null) {
            unit.updateUnitNumber(unitNumber);
        }
        if (isOccupied != null) {
            unit.updateIsOccupied(isOccupied);
        }
        if (memo != null) {
            unit.updateMemo(memo);
        }

        return unitRepository.save(unit);
    }

    /**
     * 세대 삭제
     */
    public void deleteUnit(Long unitId, Admin admin) {
        Unit unit = getAuthorizedUnit(unitId, admin);
        unitRepository.delete(unit);
    }

    /**
     * ⭐ 세대 순서(orderIndex) 변경
     */
    @Transactional
    public void updateUnitOrder(
            Long buildingId,
            Admin admin,
            List<UnitOrderUpdateRequest.UnitOrderDto> orders
    ) {
        BuildingEntity building = getAuthorizedBuilding(buildingId, admin);

        for (UnitOrderUpdateRequest.UnitOrderDto dto : orders) {
            Unit unit = unitRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new IllegalArgumentException("세대를 찾을 수 없습니다"));

            // 해당 건물 소속인지 검증
            if (!unit.getBuilding().equals(building)) {
                throw new AccessDeniedException("해당 건물의 세대가 아닙니다");
            }

            unit.updateOrderIndex(dto.getOrderIndex());
        }
        // @Transactional 이므로 save 호출 없이도 flush 됨
    }

    /**
     * ===== 공통 메서드 =====
     * 건물 존재 + 관리자 소유 검증
     */
    private BuildingEntity getAuthorizedBuilding(Long buildingId, Admin admin) {
        BuildingEntity building = buildingService.getBuildingById(buildingId);

        if (!building.getAdmin().equals(admin)) {
            throw new AccessDeniedException("해당 건물에 대한 접근 권한이 없습니다");
        }

        return building;
    }

    /**
     * ===== 공통 메서드 =====
     * 세대 존재 + 관리자 소유 검증
     */
    private Unit getAuthorizedUnit(Long unitId, Admin admin) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("세대를 찾을 수 없습니다"));

        BuildingEntity building = unit.getBuilding();

        if (!building.getAdmin().equals(admin)) {
            throw new AccessDeniedException("해당 세대에 대한 접근 권한이 없습니다");
        }

        return unit;
    }

    @Transactional(readOnly = true)
    public List<UnitStatusResponse> getUnitStatuses(Long buildingId, Admin admin) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("건물을 찾을 수 없습니다."));

        // 소유 검증
        if (!building.getAdmin().equals(admin)) {
            throw new IllegalStateException("권한 없음");
        }

        List<Unit> units =
                unitRepository.findByBuildingOrderByFloorDescOrderIndexAsc(building);

        return units.stream()
                .map(unit -> {
                    TenantBuilding approvedTB =
                            tenantBuildingRepository
                                    .findByUnitAndApprovedTrue(unit)
                                    .orElse(null);

                    return new UnitStatusResponse(unit, approvedTB);
                })
                .toList();
    }


}
