package com.bizmetrics.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bizmetrics.auth.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCnpj(String cnpj);

}
