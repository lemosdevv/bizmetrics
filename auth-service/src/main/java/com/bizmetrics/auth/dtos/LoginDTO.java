package com.bizmetrics.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank
    String usernameOrEmail,

    @NotBlank
    String password
) {

}
