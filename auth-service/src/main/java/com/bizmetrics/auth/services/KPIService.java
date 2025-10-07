package com.bizmetrics.auth.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizmetrics.auth.dtos.KPIDTO;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.models.KPI;
import com.bizmetrics.auth.repositories.CompanyRepository;
import com.bizmetrics.auth.repositories.KPIRepository;

import jakarta.transaction.Transactional;

@Service
public class KPIService {

    @Autowired
    private KPIRepository kpiRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public KPI createKPI(KPIDTO kpi){
        Company company = companyRepository.findById(kpi.companyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        KPI newKPI = new KPI();
        newKPI.setName(kpi.name());
        newKPI.setDescription(kpi.description());
        newKPI.setTargetValue(kpi.targetValue());
        newKPI.setMeta(kpi.meta());
        newKPI.setPeriodicity(kpi.periodicity());
        newKPI.setCompany(company);

        return kpiRepository.save(newKPI);
    }

    public List<KPI> findAll(){
        return kpiRepository.findAll();
    }

    public KPI findById(Long id){
        return kpiRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
    }

    public List<KPI> findByCompanyId(Long companyId){
        return kpiRepository.findByCompanyId(companyId);
    }
    
    @Transactional
    public KPI updateKPI(Long id, KPIDTO kpiDetails){
        KPI kpi = kpiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("KPI not found"));

        Company company = companyRepository.findById(kpiDetails.companyId())
            .orElseThrow(() -> new RuntimeException("Company not found"));

        kpi.setName(kpiDetails.name());
        kpi.setDescription(kpiDetails.description());
        kpi.setTargetValue(kpiDetails.targetValue());
        kpi.setMeta(kpiDetails.meta());
        kpi.setPeriodicity(kpiDetails.periodicity());

        return kpiRepository.save(kpi);
    }

    public void deleteKPI(Long id){
        KPI kpi = kpiRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
        kpiRepository.delete(kpi);
    }
}
