package com.team.house.housetalk.payment.service;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import com.team.house.housetalk.payment.dto.PaymentPeriodResponse;
import com.team.house.housetalk.payment.dto.PaymentPeriodUpdateRequest;
import com.team.house.housetalk.payment.entity.PaymentPeriod;
import com.team.house.housetalk.payment.entity.PaymentStatus;
import com.team.house.housetalk.payment.repository.PaymentPeriodRepository;
import com.team.house.housetalk.payment.repository.PaymentStatusRepository;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.tenant.repository.TenantBuildingRepository;
import com.team.house.housetalk.unit.entity.Unit;
import com.team.house.housetalk.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentPeriodService {

    private final PaymentPeriodRepository paymentPeriodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final BuildingRepository buildingRepository;
    private final UnitRepository unitRepository;
    private final TenantBuildingRepository tenantBuildingRepository;

    /**
     * 납부 기간 생성
     *
     * 1️⃣ PaymentPeriod 생성
     * 2️⃣ 해당 건물의 모든 세대(Unit) 조회
     * 3️⃣ 세대별 PaymentStatus 생성
     *    - 생성 시점의 세입자 이름/전화번호 스냅샷 저장
     */
    @Transactional
    public PaymentPeriod createPaymentPeriod(
            Long buildingId,
            int year,
            int month,
            String title
    ) {
        // 1️⃣ 건물 조회
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 건물입니다."));

        // 2️⃣ 납부 기간 생성
        PaymentPeriod paymentPeriod = new PaymentPeriod(building, year, month, title);
        paymentPeriodRepository.save(paymentPeriod);

        // 3️⃣ 해당 건물의 모든 세대 조회
        List<Unit> units = unitRepository.findByBuildingId(buildingId);

        // 4️⃣ 세대별 PaymentStatus 생성 (세입자 스냅샷 포함)
        for (Unit unit : units) {

            boolean exists = paymentStatusRepository
                    .existsByPaymentPeriodIdAndUnitId(paymentPeriod.getId(), unit.getId());

            if (exists) {
                continue;
            }

            // 🔥 생성 시점 기준 승인된 세입자 조회
            Optional<TenantBuilding> tenantBuildingOpt =
                    tenantBuildingRepository.findByUnitAndApprovedTrue(unit);


            PaymentStatus status;

            if (tenantBuildingOpt.isPresent()) {
                TenantBuilding tb = tenantBuildingOpt.get();

                status = new PaymentStatus(
                        paymentPeriod,
                        unit,
                        tb.getName(),
                        tb.getPhoneNumber()
                );
            } else {
                // 공실
                status = new PaymentStatus(paymentPeriod, unit);
            }

            paymentStatusRepository.save(status);
        }

        return paymentPeriod;
    }

    /**
     * 특정 건물의 납부 기간 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentPeriod> getPaymentPeriods(Long buildingId) {
        return paymentPeriodRepository
                .findByBuildingIdOrderByYearDescMonthDesc(buildingId);
    }

    @Transactional(readOnly = true)
    public Page<PaymentPeriodResponse> getPaymentPeriods(
            Long buildingId,
            String keyword,
            Pageable pageable
    ) {
        Page<PaymentPeriod> page = paymentPeriodRepository
                .findByBuildingIdAndTitleContainingOrderByYearDescMonthDesc(
                        buildingId,
                        keyword == null ? "" : keyword,
                        pageable
                );

        return page.map(period -> {
            int paidRate = calculatePaidRate(period.getId());
            return PaymentPeriodResponse.from(period, paidRate);
        });
    }


    public PaymentPeriod getPaymentPeriod(Long id) {
        return paymentPeriodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("납부 기간 없음"));
    }

    @Transactional(readOnly = true)
    public int calculatePaidRate(Long paymentPeriodId) {
        List<PaymentStatus> statuses =
                paymentStatusRepository.findByPaymentPeriodId(paymentPeriodId);

        if (statuses.isEmpty()) return 0;

        long paidCount = statuses.stream()
                .filter(PaymentStatus::isPaid)
                .count();

        return (int) Math.round((paidCount * 100.0) / statuses.size());
    }

    /**
     * 납부 기간 수정
     */
    @Transactional
    public PaymentPeriod updatePaymentPeriod(
            Long buildingId,
            Long paymentPeriodId,
            PaymentPeriodUpdateRequest request
    ) {
        PaymentPeriod period = paymentPeriodRepository
                .findByIdAndBuildingId(paymentPeriodId, buildingId)
                .orElseThrow(() -> new IllegalArgumentException("납부 기간 없음"));

        // ❗ year/month 중복 체크 (자기 자신 제외)
        boolean exists = paymentPeriodRepository
                .existsByBuildingIdAndYearAndMonthAndIdNot(
                        buildingId,
                        request.getYear(),
                        request.getMonth(),
                        paymentPeriodId
                );

        if (exists) {
            throw new IllegalStateException("이미 존재하는 납부 기간입니다");
        }

        period.update(
                request.getYear(),
                request.getMonth(),
                request.getTitle()
        );

        return period;
    }

    /**
     * 납부 기간 삭제
     */
    @Transactional
    public void deletePaymentPeriod(
            Long buildingId,
            Long paymentPeriodId
    ) {
        PaymentPeriod period = paymentPeriodRepository
                .findByIdAndBuildingId(paymentPeriodId, buildingId)
                .orElseThrow(() -> new IllegalArgumentException("납부 기간 없음"));

        // 🔥 payment_status는 cascade + orphanRemoval 로 자동 삭제
        paymentPeriodRepository.delete(period);
    }








}
