package com.team.house.housetalk.invite.dto;

import lombok.Getter;

@Getter
public class InviteCreateResponse {

    private final String inviteCode;

    public InviteCreateResponse(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
