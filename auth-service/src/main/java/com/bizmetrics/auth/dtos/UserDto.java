package com.bizmetrics.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserDto(
    
@NotBlank @Size(max = 50) String username,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotBlank @Email @Size(max = 100) String email,
    @NotNull @Positive Long companyId

) {

}
