package com.api.policy_management.repository;

import com.api.policy_management.model.Policy;
import com.api.policy_management.model.PolicyStatus;
import com.api.policy_management.model.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByTypeAndStatus(PolicyType type, PolicyStatus status);

    List<Policy> findByType(PolicyType type);

    List<Policy> findByStatus(PolicyStatus status);
}
