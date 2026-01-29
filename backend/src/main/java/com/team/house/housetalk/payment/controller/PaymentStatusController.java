package com.team.house.housetalk.payment.controller;

import com.team.house.housetalk.payment.dto.PaymentStatusResponse;
import com.team.house.housetalk.payment.repository.PaymentStatusRepository;
import com.team.house.housetalk.payment.service.PaymentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payments")
public class PaymentStatusController {

    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentStatusService paymentStatusService;

    /**
     * 특정 납부 기간의 세대별 납부 상태 조회
     *
     * GET /api/admin/payments/periods/{paymentPeriodId}/statuses
     */
    @GetMapping("/periods/{paymentPeriodId}/statuses")
    public List<PaymentStatusResponse> getPaymentStatuses(
            @PathVariable Long paymentPeriodId
    ) {
        return paymentStatusRepository.findByPaymentPeriodId(paymentPeriodId)
                .stream()
                .map(PaymentStatusResponse::from)
                .toList();
    }

    /**
     * 납부 상태 토글
     *
     * POST /api/admin/payments/statuses/{paymentStatusId}/toggle
     */
    @PostMapping("/statuses/{paymentStatusId}/toggle")
    public void togglePaymentStatus(
            @PathVariable Long paymentStatusId
    ) {
        paymentStatusService.togglePaymentStatus(paymentStatusId);
    }
}
