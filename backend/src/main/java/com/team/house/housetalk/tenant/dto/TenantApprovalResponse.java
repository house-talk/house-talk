package com.team.house.housetalk.tenant.dto;

import com.team.house.housetalk.tenant.entity.TenantBuilding;
import lombok.Getter;

@Getter
public class TenantApprovalResponse {

    private Long requestId;
    private String buildingName;
    private String unitNumber;
    private String phoneNumber;
    private String name;

    public TenantApprovalResponse(TenantBuilding tenantBuilding) {
        this.requestId = tenantBuilding.getId();
        this.buildingName = tenantBuilding.getBuilding().getName();
        this.unitNumber = tenantBuilding.getUnit().getUnitNumber();
        this.phoneNumber = tenantBuilding.getPhoneNumber();
        this.name = tenantBuilding.getName();
    }
}
