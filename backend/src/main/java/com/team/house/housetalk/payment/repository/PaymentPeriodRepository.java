package com.team.house.housetalk.payment.repository;

import com.team.house.housetalk.payment.entity.PaymentPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentPeriodRepository extends JpaRepository<PaymentPeriod, Long> {

    /**
     * 특정 건물의 납부 기간 목록 조회
     * (관리자 payments 탭에서 사용)
     */
    List<PaymentPeriod> findByBuildingIdOrderByYearDescMonthDesc(Long buildingId);

    Page<PaymentPeriod> findByBuildingIdAndTitleContainingOrderByYearDescMonthDesc(
            Long buildingId,
            String keyword,
            Pageable pageable
    );

    boolean existsByBuildingIdAndYearAndMonthAndIdNot(
            Long buildingId,
            int year,
            int month,
            Long id
    );

    Optional<PaymentPeriod> findByIdAndBuildingId(
            Long id,
            Long buildingId
    );




}
