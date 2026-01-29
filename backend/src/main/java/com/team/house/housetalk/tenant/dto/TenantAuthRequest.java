package com.team.house.housetalk.tenant.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TenantAuthRequest {

    private String name;
    private String password;
    private String phoneNumber;
    private boolean newUser;
}
