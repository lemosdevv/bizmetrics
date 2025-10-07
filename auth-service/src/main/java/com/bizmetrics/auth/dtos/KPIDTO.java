package com.bizmetrics.auth.dtos;

import java.math.BigDecimal;

import com.bizmetrics.auth.models.enums.Periodicity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record KPIDTO(
    
@NotBlank String name,
    @NotBlank String description,
    @NotNull BigDecimal targetValue,
    @NotNull BigDecimal meta,
    @NotNull Periodicity periodicity,
    @NotNull @Positive Long companyId
) {

}
