package com.team.house.housetalk.building.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.invite.entity.Invite;
import com.team.house.housetalk.notice.entity.Notice;
import com.team.house.housetalk.payment.entity.PaymentPeriod;
import com.team.house.housetalk.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "building")
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 건물을 관리하는 관리자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    /**
     * 건물 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 건물 주소
     */
    @Column(nullable = false, length = 255)
    private String address;

    /**
     * 건물 기준 총 층수
     */
    @Column(name = "total_floors")
    private Integer totalFloors;

    /**
     * 건물 기준 총 세대 수
     */
    @Column(name = "total_units")
    private Integer totalUnits;

    /**
     * 🔥 이 건물의 세대 목록 (핵심)
     *
     * building 삭제 → unit 자동 삭제
     */
    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Unit> units = new ArrayList<>();

    /**
     * 🔥 이 건물의 납부 기간 목록
     *
     * building 삭제 → payment_period 삭제
     * → payment_status까지 연쇄 삭제
     */
    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<PaymentPeriod> paymentPeriods = new ArrayList<>();

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Invite> invites = new ArrayList<>();

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Notice> notices = new ArrayList<>();



    /**
     * 생성 / 수정 시각
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ==============================
       JPA Lifecycle
    ============================== */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ==============================
       생성 메서드
    ============================== */

    public static BuildingEntity create(
            Admin admin,
            String name,
            String address,
            Integer totalFloors,
            Integer totalUnits
    ) {
        BuildingEntity building = new BuildingEntity();
        building.admin = admin;
        building.name = name;
        building.address = address;
        building.totalFloors = totalFloors;
        building.totalUnits = totalUnits;
        return building;
    }

    public void update(
            String name,
            String address,
            Integer totalFloors,
            Integer totalUnits
    ) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (address != null && !address.isBlank()) {
            this.address = address;
        }
        if (totalFloors != null && totalFloors > 0) {
            this.totalFloors = totalFloors;
        }
        if (totalUnits != null && totalUnits > 0) {
            this.totalUnits = totalUnits;
        }
    }
}
