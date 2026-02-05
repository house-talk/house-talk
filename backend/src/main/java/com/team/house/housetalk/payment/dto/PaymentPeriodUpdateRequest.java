package com.team.house.housetalk.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentPeriodUpdateRequest {

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;

    @NotBlank
    private String title;
}
