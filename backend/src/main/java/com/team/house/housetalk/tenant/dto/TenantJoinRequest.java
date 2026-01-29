package com.team.house.housetalk.tenant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TenantJoinRequest {

    private String inviteCode;

    private String name;
    private String phoneNumber;
    private String unitNumber;
}
