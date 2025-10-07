package com.bizmetrics.auth.controllers;

import com.bizmetrics.auth.dtos.CompanyDTO;
import com.bizmetrics.auth.models.Company;
import com.bizmetrics.auth.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Companies", description = "Manage companies")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Create a new company")
    @PostMapping
    public ResponseEntity<Company> create(@Valid @RequestBody CompanyDTO dto) {
        Company saved = companyService.createCompany(dto);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location)
                .eTag(buildWeakEtag(saved.getVersion()))
                .body(saved);
    }

    @Operation(summary = "List companies (paginated)")
    @GetMapping
    public ResponseEntity<Page<Company>> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<Company> page = companyService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get company by id")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getById(@PathVariable Long id) {
        Company c = companyService.findById(id);
        return ResponseEntity.ok()
                .eTag(buildWeakEtag(c.getVersion()))
                .body(c);
    }

    @Operation(summary = "Update a company")
    @PutMapping("/{id}")
    public ResponseEntity<Company> update(
            @PathVariable Long id,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @Valid @RequestBody CompanyDTO dto) {

        Company current = companyService.findById(id);
        // Se If-Match vier, aplicamos pré-condição
        if (ifMatch != null) {
            Long expectedVersion = parseIfMatchVersion(ifMatch);
            if (expectedVersion == null || !expectedVersion.equals(current.getVersion())) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }

        Company updated = companyService.updateCompany(id, dto);
        return ResponseEntity.ok()
                .eTag(buildWeakEtag(updated.getVersion()))
                .body(updated);
    }

    @Operation(summary = "Delete a company")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {

        Company current = companyService.findById(id);
        if (ifMatch != null) {
            Long expectedVersion = parseIfMatchVersion(ifMatch);
            if (expectedVersion == null || !expectedVersion.equals(current.getVersion())) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }

        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    /** Constrói ETag fraca a partir da versão (ex.: W/"3") */
    private static String buildWeakEtag(Long version) {
        return version == null ? null : "W/\"" + version + "\"";
    }

    /** Aceita If-Match como W/"{n}" ou como número puro. */
    private static Long parseIfMatchVersion(String ifMatch) {
        try {
            if (ifMatch == null) return null;
            String v = ifMatch.trim();
            if (v.startsWith("W/\"") && v.endsWith("\"")) {
                v = v.substring(3, v.length() - 1);
            }
            return Long.parseLong(v);
        } catch (Exception e) {
            return null;
        }
    }
}