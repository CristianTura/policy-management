package com.api.policy_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "risks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private String propertyAddress;

    @Column(nullable = false)
    private String insuredName;

    @Column(nullable = false)
    private String beneficiaryName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskStatus status;

    public void cancel() {
        this.status = RiskStatus.CANCELADO;
    }

    public boolean isCancelled() {
        return RiskStatus.CANCELADO == status;
    }
}
