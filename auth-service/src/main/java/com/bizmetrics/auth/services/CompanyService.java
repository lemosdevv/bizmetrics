package com.bizmetrics.auth.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizmetrics.auth.dtos.CompanyDTO;
import com.bizmetrics.auth.excecoes.BusinessExcepetion;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.repositories.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    
    public Company createCompany(CompanyDTO company){
        if(companyRepository.existsByCnpj(company.cnpj())){
            throw new BusinessExcepetion("CNPJ already exists");
        }

        Company newCompany = new Company();
        newCompany.setName(company.name());
        newCompany.setCnpj(company.cnpj());
        newCompany.setAddress(company.address());
        newCompany.setSector(company.sector());
        newCompany.setEmail(company.email());
        newCompany.setPhoneNumber(company.phoneNumber());

        return companyRepository.save(newCompany);
    }

    public List<Company> findAll(){
        return companyRepository.findAll();
    }

    public Company findById(Long id){
        return companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    public void deleteCompany(Long id){
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        companyRepository.delete(company);
    }
}
