package com.team.house.housetalk.unit.dto;

import lombok.Getter;

@Getter
public class UnitBulkCreateRequest {

    private Integer floor;        // 층수 (예: 5)
    private Integer startUnit;    // 시작 호수 (예: 501)
    private Integer endUnit;      // 끝 호수 (예: 509)
    private Boolean isOccupied;   // 기본 입주 상태
    private String memo;          // 공통 메모 (선택)
}
