package com.bizmetrics.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyDTO(

    @NotBlank
    @Size(max = 100)
    String name,

    @NotBlank
    @Size(max = 18)
    String cnpj,

    @NotBlank
    @Size(max = 100)
    String address,

    @NotBlank
    @Size(max = 50)
    String sector,

    @NotBlank
    @Size(max = 100)
    @Email
    String email,

    @NotBlank
    String phoneNumber
) {

}
