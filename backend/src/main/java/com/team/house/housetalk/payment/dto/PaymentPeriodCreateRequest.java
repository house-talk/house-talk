package com.team.house.housetalk.payment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPeriodCreateRequest {

    /**
     * 납부 연도 (ex. 2026)
     */
    private int year;

    /**
     * 납부 월 (1 ~ 12)
     */
    private int month;

    /**
     * 관리자용 제목
     * ex) "2026년 1월 납부 내역"
     */
    private String title;
}
