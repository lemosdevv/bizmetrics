package com.bizmetrics.auth.dtos;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyDTO( 
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 18) @CNPJ String cnpj,
    @NotBlank @Size(max = 100) String address,
    @NotBlank @Size(max = 50) String sector,
    @NotBlank @Size(max = 100) @Email String email,
    @NotBlank @Size(max = 30) @Pattern(regexp = "^[0-9()+\\-\\s]{8,20}$") String phoneNumber

) {

}
