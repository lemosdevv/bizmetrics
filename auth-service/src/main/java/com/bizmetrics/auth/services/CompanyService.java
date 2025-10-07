package com.bizmetrics.auth.services;

import com.bizmetrics.auth.dtos.CompanyDTO;
import com.bizmetrics.auth.excecoes.BusinessException;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.repositories.CompanyRepository;
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
public class CompanyService {

    private final CompanyRepository companyRepository;

    @CacheEvict(cacheNames = {"companyById", "companyList"}, allEntries = true)
    public Company createCompany(@Valid CompanyDTO dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("CNPJ already exists");
        }
        if (companyRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email already exists");
        }

        Company c = new Company();
        c.setName(dto.name());
        c.setCnpj(dto.cnpj());
        c.setAddress(dto.address());
        c.setSector(dto.sector());
        c.setEmail(dto.email());
        c.setPhoneNumber(dto.phoneNumber());

        Company saved = companyRepository.save(c);
        log.info("Company created id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "companyList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "companyById", key = "#id")
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    @CacheEvict(cacheNames = {"companyById", "companyList"}, allEntries = true)
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        companyRepository.delete(company);
        log.info("Company deleted id={}", id);
    }

    @CacheEvict(cacheNames = {"companyById", "companyList"}, allEntries = true)
    public Company updateCompany(Long id, @Valid CompanyDTO dto) {
        Company c = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // validações simples (ajuste conforme sua regra)
        if (!c.getCnpj().equals(dto.cnpj()) && companyRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("CNPJ already exists");
        }
        if (!c.getEmail().equals(dto.email()) && companyRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email already exists");
        }

        c.setName(dto.name());
        c.setCnpj(dto.cnpj());
        c.setAddress(dto.address());
        c.setSector(dto.sector());
        c.setEmail(dto.email());
        c.setPhoneNumber(dto.phoneNumber());

        Company saved = companyRepository.save(c);
        log.info("Company updated id={}", saved.getId());
        return saved;
    }
}