package com.team.house.housetalk.building.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BuildingCreateRequest {

    private String name;
    private String address;
    private Integer totalFloors;
    private Integer totalUnits;
}