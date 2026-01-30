package com.team.house.housetalk.unit.repository;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    /**
     * 특정 건물에 속한 세대 목록 조회
     */
    List<Unit> findByBuildingId(Long buildingId);

    /**
     * 특정 건물 내 호수 중복 체크
     */
    Optional<Unit> findByBuildingAndUnitNumber(
            BuildingEntity building,
            String unitNumber
    );

    int countByBuildingAndFloor(BuildingEntity building, Integer floor);

    List<Unit> findByBuildingOrderByFloorDescOrderIndexAsc(BuildingEntity building);



}
