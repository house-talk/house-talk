package com.team.house.housetalk.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.house.housetalk.building.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_period")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 납부 대상 건물
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnore // 🔥 Building → PaymentPeriod → Building 순환 방지
    private BuildingEntity building;

    /**
     * 납부 연도 (ex. 2026)
     */
    @Column(nullable = false)
    private int year;

    /**
     * 납부 월 (1 ~ 12)
     */
    @Column(nullable = false)
    private int month;

    /**
     * 관리자용 제목
     * ex) "2026년 1월 납부 내역"
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 🔥 이 납부 기간의 세대별 납부 상태
     *
     * - payment_period 삭제 시 payment_status 자동 삭제
     * - building 삭제 → payment_period 삭제 → payment_status 삭제
     */
    @OneToMany(
            mappedBy = "paymentPeriod",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PaymentStatus> paymentStatuses = new ArrayList<>();

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
       생성자
    =============================== */

    public PaymentPeriod(BuildingEntity building, int year, int month, String title) {
        this.building = building;
        this.year = year;
        this.month = month;
        this.title = title;
    }

    public void update(int year, int month, String title) {
        this.year = year;
        this.month = month;
        this.title = title;
    }

}
