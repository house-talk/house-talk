package com.team.house.housetalk.tenant.dto;

import com.team.house.housetalk.tenant.entity.Tenant;
import lombok.Getter;

@Getter
public class TenantMeResponse {

    private final String name;
    private final String phoneNumber;

    public TenantMeResponse(Tenant tenant) {
        this.name = tenant.getName();
        this.phoneNumber = tenant.getPhoneNumber();
    }
}
