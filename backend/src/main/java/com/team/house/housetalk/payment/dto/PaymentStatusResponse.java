package com.team.house.housetalk.payment.dto;

import com.team.house.housetalk.payment.entity.PaymentStatus;
import com.team.house.housetalk.unit.entity.Unit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentStatusResponse {

    private Long paymentStatusId;

    // 세대 정보
    private Long unitId;
    private int floor;
    private String unitNumber;

    // 🔥 생성 시점 세입자 스냅샷
    private String tenantName;
    private String tenantPhoneNumber;

    // 납부 상태
    private boolean isPaid;

    public PaymentStatusResponse(
            Long paymentStatusId,
            Long unitId,
            int floor,
            String unitNumber,
            String tenantName,
            String tenantPhoneNumber,
            boolean isPaid
    ) {
        this.paymentStatusId = paymentStatusId;
        this.unitId = unitId;
        this.floor = floor;
        this.unitNumber = unitNumber;
        this.tenantName = tenantName;
        this.tenantPhoneNumber = tenantPhoneNumber;
        this.isPaid = isPaid;
    }

    public static PaymentStatusResponse from(PaymentStatus status) {
        Unit unit = status.getUnit();

        return new PaymentStatusResponse(
                status.getId(),
                unit.getId(),
                unit.getFloor(),
                unit.getUnitNumber(),
                status.getTenantName(),          // 공실이면 null
                status.getTenantPhoneNumber(),  // 공실이면 null
                status.isPaid()
        );
    }

    /* ===============================
       프론트 편의 메서드 (선택)
       - 없어도 되지만 있으면 UX 편함
    =============================== */

    public boolean isVacant() {
        return tenantName == null || tenantName.isBlank();
    }
}
