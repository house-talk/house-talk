package com.team.house.housetalk.unit.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UnitOrderUpdateRequest {

    private List<UnitOrderDto> orders;

    @Getter
    public static class UnitOrderDto {
        private Long unitId;
        private Integer orderIndex;
    }
}
