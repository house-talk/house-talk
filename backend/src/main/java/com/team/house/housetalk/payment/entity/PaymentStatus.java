package com.team.house.housetalk.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.house.housetalk.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 납부 기간
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_period_id", nullable = false)
    @JsonIgnore // 🔥 PaymentPeriod → PaymentStatus → PaymentPeriod 순환 차단
    private PaymentPeriod paymentPeriod;

    /**
     * 대상 세대
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    /**
     * ===== 생성 시점 세입자 스냅샷 =====
     * (과거 기록 보존용)
     */
    @Column(name = "tenant_name", length = 50)
    private String tenantName;

    @Column(name = "tenant_phone_number", length = 30)
    private String tenantPhoneNumber;

    /**
     * 납부 여부
     */
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    /**
     * 관리자 체크 시각
     */
    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    /**
     * 생성 / 수정 시각
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===============================
       JPA Lifecycle
    =============================== */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ===============================
       생성자 (팩토리 역할)
    =============================== */

    /**
     * 공실 상태
     */
    public PaymentStatus(PaymentPeriod paymentPeriod, Unit unit) {
        this.paymentPeriod = paymentPeriod;
        this.unit = unit;
        this.isPaid = false;
    }

    /**
     * 세입자 스냅샷 포함 생성
     */
    public PaymentStatus(
            PaymentPeriod paymentPeriod,
            Unit unit,
            String tenantName,
            String tenantPhoneNumber
    ) {
        this.paymentPeriod = paymentPeriod;
        this.unit = unit;
        this.tenantName = tenantName;
        this.tenantPhoneNumber = tenantPhoneNumber;
        this.isPaid = false;
    }

    /* ===============================
       비즈니스 메서드
    =============================== */

    public void markPaid() {
        this.isPaid = true;
        this.checkedAt = LocalDateTime.now();
    }

    public void markUnpaid() {
        this.isPaid = false;
        this.checkedAt = null;
    }
}
