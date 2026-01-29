package com.team.house.housetalk.tenant.dto;

import com.team.house.housetalk.tenant.entity.TenantBuilding;
import lombok.Getter;

@Getter
public class TenantHomeResponse {

    private Long tenantBuildingId;
    private String buildingName;
    private String unitNumber;

    public TenantHomeResponse(TenantBuilding tenantBuilding) {
        this.tenantBuildingId = tenantBuilding.getId();
        this.buildingName = tenantBuilding.getBuilding().getName();
        this.unitNumber = tenantBuilding.getUnit().getUnitNumber();
    }
}
