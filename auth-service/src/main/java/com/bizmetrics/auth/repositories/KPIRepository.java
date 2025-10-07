package com.bizmetrics.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bizmetrics.auth.models.KPI;
import com.bizmetrics.auth.models.enums.Periodicity;


public interface KPIRepository extends JpaRepository<KPI, Long>, JpaSpecificationExecutor<KPI> {

    Page<KPI> findByCompanyId(Long companyId, Pageable pageable);

    List<KPI> findByCompanyIdAndPeriodicity(Long companyId, Periodicity periodicity);

    Optional<KPI> findByCompanyIdAndNameIgnoreCase(Long companyId, String name);

    @EntityGraph(attributePaths = "company")
    Page<KPI> findByCompanyIdAndNameContainingIgnoreCase(Long companyId, String name, Pageable pageable);
}

