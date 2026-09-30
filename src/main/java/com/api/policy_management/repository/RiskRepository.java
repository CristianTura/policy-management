package com.api.policy_management.repository;

import com.api.policy_management.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    List<Risk> findByPolicyId(Long policyId);
}
