package com.team.house.housetalk.payment.controller;

import com.team.house.housetalk.payment.dto.PaymentPeriodCreateRequest;
import com.team.house.housetalk.payment.dto.PaymentPeriodResponse;
import com.team.house.housetalk.payment.entity.PaymentPeriod;
import com.team.house.housetalk.payment.service.PaymentPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/buildings/{buildingId}/payments")
public class PaymentPeriodController {

    private final PaymentPeriodService paymentPeriodService;

    /**
     * 납부 기간 생성
     */
    @PostMapping
    public PaymentPeriodResponse createPaymentPeriod(
            @PathVariable Long buildingId,
            @RequestBody PaymentPeriodCreateRequest request
    ) {
        PaymentPeriod period = paymentPeriodService.createPaymentPeriod(
                buildingId,
                request.getYear(),
                request.getMonth(),
                request.getTitle()
        );

        int paidRate = paymentPeriodService.calculatePaidRate(period.getId());

        return PaymentPeriodResponse.from(period, paidRate);
    }

    /**
     * 납부 기간 목록 조회
     */
    @GetMapping
    public Page<PaymentPeriodResponse> getPaymentPeriods(
            @PathVariable Long buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentPeriodService.getPaymentPeriods(buildingId, keyword, pageable);
    }


    /**
     * 납부 기간 단건 조회
     */
    @GetMapping("/periods/{paymentPeriodId}")
    public PaymentPeriodResponse getPaymentPeriod(
            @PathVariable Long paymentPeriodId
    ) {
        PaymentPeriod period =
                paymentPeriodService.getPaymentPeriod(paymentPeriodId);

        int paidRate =
                paymentPeriodService.calculatePaidRate(paymentPeriodId);

        return PaymentPeriodResponse.from(period, paidRate);
    }
}
