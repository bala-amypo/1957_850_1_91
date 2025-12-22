package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UrgencyPolicy;
import com.example.demo.repository.UrgencyPolicyRepository;
import com.example.demo.service.UrgencyPolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UrgencyPolicyServiceImpl implements UrgencyPolicyService {

    private final UrgencyPolicyRepository policyRepository;

    UrgencyPolicyServiceImpl(UrgencyPolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public UrgencyPolicy createPolicy(UrgencyPolicy policy) {
        return policyRepository.save(policy);
    }

    @Override
    public UrgencyPolicy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    }

    @Override
    public List<UrgencyPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public UrgencyPolicy updatePolicy(Long id, UrgencyPolicy policy) {
        UrgencyPolicy existing = getPolicyById(id);

        existing.setPolicyName(policy.getPolicyName());
        existing.setKeyword(policy.getKeyword());
        existing.setUrgencyOverride(policy.getUrgencyOverride());

        return policyRepository.save(existing);
    }

    @Override
    public void deletePolicy(Long id) {
        policyRepository.delete(getPolicyById(id));
    }
}
