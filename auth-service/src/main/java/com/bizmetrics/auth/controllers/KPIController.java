// src/main/java/com/bizmetrics/auth/controllers/KPIController.java
package com.bizmetrics.auth.controllers;

import com.bizmetrics.auth.dtos.KPIDTO;
import com.bizmetrics.auth.models.KPI;
import com.bizmetrics.auth.services.KPIService;
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

@Tag(name = "KPIs", description = "Manage KPIs")
@RestController
@RequestMapping("/api/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIService kpiService;

    @Operation(summary = "Create a new KPI")
    @PostMapping
    public ResponseEntity<KPI> create(@Valid @RequestBody KPIDTO dto) {
        KPI saved = kpiService.createKPI(dto);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location)
                .eTag(buildWeakEtag(saved.getVersion()))
                .body(saved);
    }

    @Operation(summary = "List KPIs (paginated)")
    @GetMapping
    public ResponseEntity<Page<KPI>> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(kpiService.findAll(pageable));
    }

    @Operation(summary = "Get KPI by id")
    @GetMapping("/{id}")
    public ResponseEntity<KPI> getById(@PathVariable Long id) {
        KPI kpi = kpiService.findById(id);
        return ResponseEntity.ok()
                .eTag(buildWeakEtag(kpi.getVersion()))
                .body(kpi);
    }

    @Operation(summary = "List KPIs by company (paginated)")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<KPI>> listByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(kpiService.findByCompanyId(companyId, pageable));
    }

    @Operation(summary = "Update a KPI")
    @PutMapping("/{id}")
    public ResponseEntity<KPI> update(
            @PathVariable Long id,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @Valid @RequestBody KPIDTO dto) {

        KPI current = kpiService.findById(id);
        if (ifMatch != null) {
            Long expectedVersion = parseIfMatchVersion(ifMatch);
            if (expectedVersion == null || !expectedVersion.equals(current.getVersion())) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }

        KPI updated = kpiService.updateKPI(id, dto);
        return ResponseEntity.ok()
                .eTag(buildWeakEtag(updated.getVersion()))
                .body(updated);
    }

    @Operation(summary = "Delete a KPI")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {

        KPI current = kpiService.findById(id);
        if (ifMatch != null) {
            Long expectedVersion = parseIfMatchVersion(ifMatch);
            if (expectedVersion == null || !expectedVersion.equals(current.getVersion())) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }

        kpiService.deleteKPI(id);
        return ResponseEntity.noContent().build();
    }

    private static String buildWeakEtag(Long version) {
        return version == null ? null : "W/\"" + version + "\"";
    }

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