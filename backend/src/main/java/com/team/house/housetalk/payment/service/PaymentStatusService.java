package com.team.house.housetalk.payment.service;

import com.team.house.housetalk.payment.entity.PaymentStatus;
import com.team.house.housetalk.payment.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;

    /**
     * 납부 상태 토글
     *
     * - 미납 → 납부 완료
     * - 납부 완료 → 미납
     */
    @Transactional
    public void togglePaymentStatus(Long paymentStatusId) {

        PaymentStatus status = paymentStatusRepository.findById(paymentStatusId)
                .orElseThrow(() -> new IllegalArgumentException("납부 상태를 찾을 수 없습니다."));

        if (status.isPaid()) {
            status.markUnpaid();
        } else {
            status.markPaid();
        }
    }
}
