package com.bizmetrics.auth.services;

import com.bizmetrics.auth.dtos.UserDto;
import com.bizmetrics.auth.excecoes.BusinessException;
import com.bizmetrics.auth.excecoes.ResourceNotFoundException;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.models.User;
import com.bizmetrics.auth.repositories.CompanyRepository;
import com.bizmetrics.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @CacheEvict(cacheNames = {"userById", "userListByCompany"}, allEntries = true)
    public User createUser(@Valid UserDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email already exists");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        User u = new User();
        u.setUsername(dto.username());
        u.setEmail(dto.email());
        u.setPassword(passwordEncoder.encode(dto.password()));
        u.setCompany(company);

        User saved = userRepository.save(u);
        log.info("User created id={} companyId={}", saved.getId(), company.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userById", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userListByCompany", key = "#companyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<User> getUsersByCompany(Long companyId, Pageable pageable) {
        return userRepository.findByCompanyId(companyId, pageable);
    }

    @CacheEvict(cacheNames = {"userById", "userListByCompany"}, allEntries = true)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        log.info("User deleted id={}", id);
    }
}