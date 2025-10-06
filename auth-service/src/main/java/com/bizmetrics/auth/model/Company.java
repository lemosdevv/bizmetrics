package com.bizmetrics.auth.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity

@Table(name = "companies", uniqueConstraints = {
    @UniqueConstraint(columnNames = "cnpj")
})
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size
    private String name;

    @NotBlank
    @Size(max = 18)
    private String cnpj;

    @NotBlank
    @Size(max = 100)
    private String address;

    @NotBlank
    @Size(max = 50)
    private String sector;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    private String phoneNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
