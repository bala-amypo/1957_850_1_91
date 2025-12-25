package com.example.demo.service;

import com.example.demo.model.UrgencyPolicy;
import java.util.List;

public interface UrgencyPolicyService {
    UrgencyPolicy createPolicy(UrgencyPolicy policy);
    UrgencyPolicy getPolicy(Long id);
    List<UrgencyPolicy> getAllPolicies();
}