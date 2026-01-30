package com.team.house.housetalk.unit.dto;

import com.team.house.housetalk.unit.entity.Unit;
import lombok.Getter;

@Getter
public class UnitResponse {

    private final Long id;
    private final Integer floor;
    private final String unitNumber;
    private final Boolean isOccupied;
    private final String memo;

    public UnitResponse(Unit unit) {
        this.id = unit.getId();
        this.floor = unit.getFloor();
        this.unitNumber = unit.getUnitNumber();
        this.isOccupied = unit.getIsOccupied();
        this.memo = unit.getMemo();
    }
}
