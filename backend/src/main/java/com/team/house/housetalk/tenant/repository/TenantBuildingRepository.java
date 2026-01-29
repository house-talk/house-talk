package com.team.house.housetalk.tenant.repository;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.tenant.entity.Tenant;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantBuildingRepository extends JpaRepository<TenantBuilding, Long> {

    /**
     * 특정 건물의 승인 대기 세입자 목록
     * (관리자 승인 화면용)
     */
    List<TenantBuilding> findByBuildingAndApprovedFalse(BuildingEntity building);

    /**
     * 특정 세입자가 승인된 집 목록
     * (세입자 홈 화면용)
     */
    List<TenantBuilding> findByTenantAndApprovedTrue(Tenant tenant);

    /**
     * 중복 요청 방지
     * 같은 세입자가 같은 호수로 다시 요청하는 것 방지
     */
    boolean existsByTenant_IdAndUnit_Id(Long tenantId, Long unitId);

    /**
     * 특정 호수에 이미 승인된 세입자가 있는지 확인
     */
    Optional<TenantBuilding> findByUnitAndApprovedTrue(Unit unit);

    /**
     * ✅ 세입자 전용 건물 상세 조회
     * - tenantBuildingId
     * - 본인 소유
     * - 승인된 건물만
     */
    Optional<TenantBuilding> findByIdAndTenantAndApprovedTrue(
            Long tenantBuildingId,
            Tenant tenant
    );

    boolean existsByUnitAndApprovedTrue(Unit unit);
}
