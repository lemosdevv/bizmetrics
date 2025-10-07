package com.bizmetrics.auth.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bizmetrics.auth.models.User;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // útil para login
    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Page<User> findByCompanyId(Long companyId, Pageable pageable);

    @EntityGraph(attributePaths = "company")
    Page<User> findByCompanyIdAndUsernameContainingIgnoreCase(Long companyId, String username, Pageable pageable);
}

