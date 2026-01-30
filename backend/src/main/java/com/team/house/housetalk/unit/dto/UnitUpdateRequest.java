package com.team.house.housetalk.unit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnitUpdateRequest {

    /**
     * 층수
     */
    private Integer floor;

    /**
     * 호수 (ex: 101, 202)
     */
    private String unitNumber;

    /**
     * 입주 여부
     */
    private Boolean isOccupied;

    /**
     * 메모
     */
    private String memo;
}
