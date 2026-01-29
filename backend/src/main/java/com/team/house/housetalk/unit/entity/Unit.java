package com.team.house.housetalk.unit.entity;

import com.team.house.housetalk.building.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 건물
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingEntity building;

    /**
     * 층수
     */
    @Column(nullable = false)
    private Integer floor;

    /**
     * 호수 (ex: 101, 202)
     */
    @Column(name = "unit_number", length = 10, nullable = false)
    private String unitNumber;

    /**
     * 같은 층 내 표시 순서
     */
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    /**
     * 세대 사용 여부
     */
    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied;

    /**
     * 메모
     */
    @Column(length = 255)
    private String memo;

    /**
     * 생성일
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* =====================
       생성 / 갱신 라이프사이클
       ===================== */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* =====================
       생성 메서드
       ===================== */

    public static Unit create(
            BuildingEntity building,
            Integer floor,
            String unitNumber,
            Integer orderIndex,
            Boolean isOccupied,
            String memo
    ) {
        Unit unit = new Unit();
        unit.building = building;
        unit.floor = floor;
        unit.unitNumber = unitNumber;
        unit.orderIndex = orderIndex;
        unit.isOccupied = isOccupied;
        unit.memo = memo;
        return unit;
    }

    /* =====================
       수정 메서드
       ===================== */

    public void updateFloor(Integer floor) {
        this.floor = floor;
    }

    public void updateUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void updateOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void updateIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void markOccupied() {
        this.isOccupied = true;
    }

    public void markVacant() {
        this.isOccupied = false;
    }
}
