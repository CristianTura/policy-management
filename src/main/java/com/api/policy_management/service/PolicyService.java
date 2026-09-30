package com.api.policy_management.service;

import com.api.policy_management.client.CoreClient;
import com.api.policy_management.dto.CreateRiskRequest;
import com.api.policy_management.dto.PolicyResponse;
import com.api.policy_management.dto.RiskResponse;
import com.api.policy_management.exception.BusinessException;
import com.api.policy_management.exception.NotFoundException;
import com.api.policy_management.model.*;
import com.api.policy_management.repository.PolicyRepository;
import com.api.policy_management.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final RiskRepository riskRepository;
    private final CoreClient coreClient;
    private final BigDecimal ipcRate;

    public PolicyService(
            PolicyRepository policyRepository,
            RiskRepository riskRepository,
            CoreClient coreClient,
            @Value("${app.ipc-rate:0.05}") BigDecimal ipcRate
    ) {
        this.policyRepository = policyRepository;
        this.riskRepository = riskRepository;
        this.coreClient = coreClient;
        this.ipcRate = ipcRate;
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> list(PolicyType tipo, PolicyStatus estado) {
        List<Policy> policies;

        if (tipo != null && estado != null) {
            policies = policyRepository.findByTypeAndStatus(tipo, estado);
        } else if (tipo != null) {
            policies = policyRepository.findByType(tipo);
        } else if (estado != null) {
            policies = policyRepository.findByStatus(estado);
        } else {
            policies = policyRepository.findAll();
        }

        return policies.stream().map(PolicyResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<RiskResponse> listRisks(Long policyId) {
        findPolicy(policyId);
        return riskRepository.findByPolicyId(policyId).stream()
                .map(RiskResponse::from)
                .toList();
    }

    public PolicyResponse renew(Long policyId) {
        Policy policy = findPolicy(policyId);

        if (policy.isCancelled()) {
            throw new BusinessException("No se puede renovar una póliza cancelada");
        }

        policy.renew(ipcRate);
        Policy saved = policyRepository.save(policy);
        coreClient.notifyUpdate(saved.getId());
        return PolicyResponse.from(saved);
    }

    public PolicyResponse cancel(Long policyId) {
        Policy policy = findPolicy(policyId);

        if (policy.isCancelled()) {
            throw new BusinessException("La póliza ya está cancelada");
        }

        // Cancela la póliza y todos sus riesgos
        policy.cancel();
        Policy saved = policyRepository.save(policy);
        coreClient.notifyUpdate(saved.getId());
        return PolicyResponse.from(saved);
    }

    public RiskResponse addRisk(Long policyId, CreateRiskRequest request) {
        Policy policy = findPolicy(policyId);

        if (policy.isCancelled()) {
            throw new BusinessException("No se pueden modificar pólizas canceladas");
        }

        if (policy.getType() != PolicyType.COLECTIVA) {
            throw new BusinessException(
                    "No se puede agregar riesgo: una póliza individual solo puede tener 1 riesgo"
            );
        }

        Risk risk = Risk.builder()
                .propertyAddress(request.direccionInmueble())
                .insuredName(request.asegurado())
                .beneficiaryName(request.beneficiario())
                .status(RiskStatus.ACTIVO)
                .build();

        policy.addRisk(risk);
        policyRepository.save(policy);
        coreClient.notifyUpdate(policyId);
        return RiskResponse.from(risk);
    }

    public RiskResponse cancelRisk(Long riskId) {
        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new NotFoundException("Riesgo no encontrado: " + riskId));

        if (risk.isCancelled()) {
            throw new BusinessException("El riesgo ya está cancelado");
        }

        Policy policy = risk.getPolicy();
        if (policy.isCancelled()) {
            throw new BusinessException("No se puede cancelar un riesgo de una póliza cancelada");
        }

        risk.cancel();
        Risk saved = riskRepository.save(risk);
        coreClient.notifyUpdate(policy.getId());
        return RiskResponse.from(saved);
    }

    private Policy findPolicy(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Póliza no encontrada: " + id));
    }
}
