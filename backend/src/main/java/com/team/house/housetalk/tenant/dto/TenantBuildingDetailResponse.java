package com.team.house.housetalk.tenant.dto;

import com.team.house.housetalk.tenant.entity.TenantBuilding;
import lombok.Getter;

@Getter
public class TenantBuildingDetailResponse {

    private Long tenantBuildingId;
    private Long buildingId;

    private String buildingName;
    private String address;

    private String unitNumber;

    private TenantBuildingDetailResponse(
            Long tenantBuildingId,
            Long buildingId,
            String buildingName,
            String address,
            String unitNumber
    ) {
        this.tenantBuildingId = tenantBuildingId;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.address = address;
        this.unitNumber = unitNumber;
    }

    /**
     * Entity → DTO 변환
     */
    public static TenantBuildingDetailResponse from(TenantBuilding tenantBuilding) {
        return new TenantBuildingDetailResponse(
                tenantBuilding.getId(),
                tenantBuilding.getBuilding().getId(),
                tenantBuilding.getBuilding().getName(),
                tenantBuilding.getBuilding().getAddress(),
                tenantBuilding.getUnit().getUnitNumber()
        );
    }
}
