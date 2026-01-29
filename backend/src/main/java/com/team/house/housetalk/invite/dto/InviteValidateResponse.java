package com.team.house.housetalk.invite.dto;

import lombok.Getter;

@Getter
public class InviteValidateResponse {

    private final Long buildingId;
    private final String buildingName;

    public InviteValidateResponse(Long buildingId, String buildingName) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
    }
}
