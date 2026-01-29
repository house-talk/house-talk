package com.team.house.housetalk.payment.dto;

import com.team.house.housetalk.payment.entity.PaymentPeriod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPeriodResponse {

    private Long id;
    private int year;
    private int month;
    private String title;

    // ✅ 추가: 납부율 (%)
    private int paidRate;

    // ✅ 생성자 수정
    public PaymentPeriodResponse(
            Long id,
            int year,
            int month,
            String title,
            int paidRate
    ) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.title = title;
        this.paidRate = paidRate;
    }

    /**
     * ⚠️ 주의
     * - 여기서는 paidRate를 직접 계산하지 않는다
     * - Service에서 계산한 값을 넘겨받는 구조가 정석
     */
    public static PaymentPeriodResponse from(
            PaymentPeriod paymentPeriod,
            int paidRate
    ) {
        return new PaymentPeriodResponse(
                paymentPeriod.getId(),
                paymentPeriod.getYear(),
                paymentPeriod.getMonth(),
                paymentPeriod.getTitle(),
                paidRate
        );
    }


}
