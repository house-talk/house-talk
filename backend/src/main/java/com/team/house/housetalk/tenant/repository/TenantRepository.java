package com.team.house.housetalk.tenant.repository;

import com.team.house.housetalk.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * tenantCode로 세입자 조회
     * (쿠키 기반 세션 식별용)
     */
    Optional<Tenant> findByTenantCode(String tenantCode);

    /**
     * ⭐ 전화번호로 세입자 조회 (인증용 ID)
     */
    Optional<Tenant> findByPhoneNumber(String phoneNumber);
}
