package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
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
        if (policy == null) {
            throw new BadRequestException("Policy not found");
        }
        return policyRepository.save(policy);
    }

    @Override
    public UrgencyPolicy getPolicy(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    }

    @Override
    public List<UrgencyPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }
}
