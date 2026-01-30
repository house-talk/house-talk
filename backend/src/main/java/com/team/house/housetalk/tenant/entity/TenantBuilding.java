package com.team.house.housetalk.tenant.entity;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.unit.entity.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TenantBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= 관계 ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingEntity building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    /* ================= 정보 ================= */

    @Column(nullable = false)
    private String name;          // 세입자 이름

    @Column(nullable = false)
    private String phoneNumber;   // 전화번호

    @Column(nullable = false)
    private boolean approved = false; //

    /* ================= 생성 ================= */

    public static TenantBuilding create(
            Tenant tenant,
            BuildingEntity building,
            Unit unit,
            String name,
            String phoneNumber
    ) {
        TenantBuilding tb = new TenantBuilding();
        tb.tenant = tenant;
        tb.building = building;
        tb.unit = unit;
        tb.name = name;
        tb.phoneNumber = phoneNumber;
        tb.approved = false;
        return tb;
    }

    /* ================= 비즈니스 로직 ================= */

    public void approve() {
        this.approved = true;
    }
}
