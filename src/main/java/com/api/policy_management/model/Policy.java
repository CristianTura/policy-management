package com.api.policy_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer coverageMonths;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyRent;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal premium;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Risk> risks = new ArrayList<>();

    public void recalculatePremium() {
        this.premium = monthlyRent
                .multiply(BigDecimal.valueOf(coverageMonths))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void renew(BigDecimal ipcRate) {
        BigDecimal factor = BigDecimal.ONE.add(ipcRate);
        this.monthlyRent = monthlyRent.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        recalculatePremium();
        this.startDate = endDate.plusDays(1);
        this.endDate = startDate.plusMonths(coverageMonths).minusDays(1);
        this.status = PolicyStatus.RENOVADA;
    }

    public void cancel() {
        this.status = PolicyStatus.CANCELADA;
        this.risks.forEach(Risk::cancel);
    }

    public void addRisk(Risk risk) {
        risks.add(risk);
        risk.setPolicy(this);
    }

    public boolean isCollective() {
        return PolicyType.COLECTIVA == type;
    }

    public boolean isCancelled() {
        return PolicyStatus.CANCELADA == status;
    }
}
