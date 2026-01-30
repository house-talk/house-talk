package com.team.house.housetalk.unit.dto;

import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.unit.entity.Unit;
import lombok.Getter;

@Getter
public class UnitStatusResponse {

    private Long unitId;
    private String unitNumber;
    private Integer floor;
    private boolean occupied;

    // ✅ 승인된 세입자 정보 (없으면 null)
    private String tenantName;
    private String tenantPhoneNumber;
    private Long tenantBuildingId;
    private String memo;

    public UnitStatusResponse(Unit unit, TenantBuilding approvedTenantBuilding) {
        this.unitId = unit.getId();
        this.unitNumber = unit.getUnitNumber();
        this.floor = unit.getFloor();
        this.occupied = unit.getIsOccupied();
        this.memo = unit.getMemo();


        if (approvedTenantBuilding != null) {
            this.tenantName = approvedTenantBuilding.getName();
            this.tenantPhoneNumber = approvedTenantBuilding.getPhoneNumber();
            this.tenantBuildingId = approvedTenantBuilding.getId();
        }
    }
}
