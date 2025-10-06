package com.bizmetrics.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bizmetrics.auth.model.KPI;

public interface KPIRepository extends JpaRepository<KPI, Long> {
    List<KPI> findByCompanyId(Long companyId);
}
