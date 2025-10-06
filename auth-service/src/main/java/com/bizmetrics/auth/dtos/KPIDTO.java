package com.bizmetrics.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KPIDTO(
    @NotBlank
    String name,

    @NotBlank
    String description,

    @NotNull
    Double targetValue,

    @NotNull
    Double meta,

    @NotBlank
    String periodicity,

    @NotNull
    Long companyId
) {

}
