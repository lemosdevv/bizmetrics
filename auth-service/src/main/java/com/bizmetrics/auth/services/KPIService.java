package com.bizmetrics.auth.services;

import com.bizmetrics.auth.dtos.KPIDTO;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.models.KPI;
import com.bizmetrics.auth.repositories.CompanyRepository;
import com.bizmetrics.auth.repositories.KPIRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional
public class KPIService {

    private final KPIRepository kpiRepository;
    private final CompanyRepository companyRepository;

    @CacheEvict(cacheNames = {"kpiById", "kpiListByCompany"}, allEntries = true)
    public KPI createKPI(@Valid KPIDTO dto) {
        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // impedir nomes duplicados por empresa
        kpiRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), dto.name())
                .ifPresent(k -> { throw new IllegalArgumentException("KPI name already exists for this company"); });

        KPI k = new KPI();
        k.setName(dto.name());
        k.setDescription(dto.description());
        k.setTargetValue(dto.targetValue()); 
        k.setMeta(dto.meta());
        k.setPeriodicity(dto.periodicity()); 
        k.setCompany(company);

        KPI saved = kpiRepository.save(k);
        log.info("KPI created id={} companyId={}", saved.getId(), company.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<KPI> findAll(Pageable pageable) {
        return kpiRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "kpiById", key = "#id")
    public KPI findById(Long id) {
        return kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "kpiListByCompany", key = "#companyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<KPI> findByCompanyId(Long companyId, Pageable pageable) {
        return kpiRepository.findByCompanyId(companyId, pageable);
    }

    @CacheEvict(cacheNames = {"kpiById", "kpiListByCompany"}, allEntries = true)
    public KPI updateKPI(Long id, @Valid KPIDTO dto) {
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // (Opcional) validar duplicidade de nome ao trocar
        if (!kpi.getName().equalsIgnoreCase(dto.name())) {
            kpiRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), dto.name())
                    .ifPresent(k -> { throw new IllegalArgumentException("KPI name already exists for this company"); });
        }

        kpi.setName(dto.name());
        kpi.setDescription(dto.description());
        kpi.setTargetValue(dto.targetValue());
        kpi.setMeta(dto.meta());
        kpi.setPeriodicity(dto.periodicity());
        kpi.setCompany(company);

        KPI saved = kpiRepository.save(kpi);
        log.info("KPI updated id={}", saved.getId());
        return saved;
    }

    @CacheEvict(cacheNames = {"kpiById", "kpiListByCompany"}, allEntries = true)
    public void deleteKPI(Long id) {
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
        kpiRepository.delete(kpi);
        log.info("KPI deleted id={}", id);
    }
}