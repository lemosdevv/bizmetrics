// src/main/java/com/bizmetrics/auth/controllers/UserController.java
package com.bizmetrics.auth.controllers;

import com.bizmetrics.auth.dtos.UserDto;
import com.bizmetrics.auth.models.User;
import com.bizmetrics.auth.services.UserService;
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

@Tag(name = "Users", description = "Manage users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserDto dto) {
        User saved = userService.createUser(dto);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        // password tem @JsonIgnore no entity -> seguro para retornar
        return ResponseEntity.created(location)
                .eTag(buildWeakEtag(saved.getVersion()))
                .body(saved);
    }

    @Operation(summary = "List users (paginated)")
    @GetMapping
    public ResponseEntity<Page<User>> list(@PageableDefault(size = 20, sort = "username") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User u = userService.getUserById(id);
        return ResponseEntity.ok()
                .eTag(buildWeakEtag(u.getVersion()))
                .body(u);
    }

    @Operation(summary = "List users by company (paginated)")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<User>> listByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByCompany(companyId, pageable));
    }

    @Operation(summary = "Delete user by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        User current = userService.getUserById(id);
        if (ifMatch != null) {
            Long expectedVersion = parseIfMatchVersion(ifMatch);
            if (expectedVersion == null || !expectedVersion.equals(current.getVersion())) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }
        userService.deleteUser(id);
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