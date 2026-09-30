package com.api.policy_management.config;

import com.api.policy_management.model.*;
import com.api.policy_management.repository.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(PolicyRepository policyRepository) {
        return args -> {
            if (policyRepository.count() > 0) {
                return;
            }

            Policy individual = Policy.builder()
                    .type(PolicyType.INDIVIDUAL)
                    .status(PolicyStatus.ACTIVA)
                    .startDate(LocalDate.now().minusMonths(2))
                    .endDate(LocalDate.now().plusMonths(10))
                    .coverageMonths(12)
                    .monthlyRent(new BigDecimal("1500000.00"))
                    .premium(new BigDecimal("18000000.00"))
                    .build();

            individual.addRisk(Risk.builder()
                    .propertyAddress("Calle 100 #15-20, Bogotá")
                    .insuredName("Ana Pérez")
                    .beneficiaryName("Carlos Ruiz")
                    .status(RiskStatus.ACTIVO)
                    .build());

            Policy collective = Policy.builder()
                    .type(PolicyType.COLECTIVA)
                    .status(PolicyStatus.ACTIVA)
                    .startDate(LocalDate.now().minusMonths(1))
                    .endDate(LocalDate.now().plusMonths(11))
                    .coverageMonths(12)
                    .monthlyRent(new BigDecimal("2500000.00"))
                    .premium(new BigDecimal("30000000.00"))
                    .build();

            collective.addRisk(Risk.builder()
                    .propertyAddress("Cra 7 #45-10, Bogotá")
                    .insuredName("Inmobiliaria Norte")
                    .beneficiaryName("Propietario Uno")
                    .status(RiskStatus.ACTIVO)
                    .build());

            collective.addRisk(Risk.builder()
                    .propertyAddress("Av. Suba #90-15, Bogotá")
                    .insuredName("Inmobiliaria Norte")
                    .beneficiaryName("Propietario Dos")
                    .status(RiskStatus.ACTIVO)
                    .build());

            policyRepository.save(individual);
            policyRepository.save(collective);
        };
    }
}
