package com.team.house.housetalk.payment.repository;

import com.team.house.housetalk.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {

    /**
     * 특정 납부 기간에 대한 모든 세대의 납부 상태 조회
     * (관리자 payments 탭 메인 화면)
     */
    List<PaymentStatus> findByPaymentPeriodId(Long paymentPeriodId);

    /**
     * 특정 납부 기간 + 세대 조합 존재 여부 확인
     * (중복 생성 방지용 - 서비스 레벨에서 사용)
     */
    boolean existsByPaymentPeriodIdAndUnitId(Long paymentPeriodId, Long unitId);

    /**
     * 특정 납부 상태 단건 조회
     * (납부 토글용)
     */
    Optional<PaymentStatus> findByPaymentPeriodIdAndUnitId(Long paymentPeriodId, Long unitId);
}
