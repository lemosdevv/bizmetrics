package com.bizmetrics.auth.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bizmetrics.auth.models.enums.Periodicity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "kpis",
    indexes = {
        @Index(name = "ix_kpis_company", columnList = "company"),
        @Index(name = "ix_kpis_name", columnList = "name")
    }
)
@Getter
@Setter
@ToString(exclude = "company")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String description;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal targetValue;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal meta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Periodicity periodicity;

    @JoinColumn(name = "company_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_kpi_company"))
    private Company company;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

}
