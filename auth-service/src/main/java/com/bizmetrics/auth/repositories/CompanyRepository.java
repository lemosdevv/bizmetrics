package com.bizmetrics.auth.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.bizmetrics.auth.models.Company;

import jakarta.persistence.LockModeType;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);

    Optional<Company> findByCnpj(String cnpj);

    Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Leitura com controle otimista (casa com @Version)
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select c from Company c where c.id = :id")
    Optional<Company> findWithLockById(Long id);

}
