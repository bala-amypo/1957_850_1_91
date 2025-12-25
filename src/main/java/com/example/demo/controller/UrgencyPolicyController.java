package com.example.demo.controller;

import com.example.demo.model.UrgencyPolicy;
import com.example.demo.service.UrgencyPolicyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policy", description = "Urgency policy management endpoints")
public class UrgencyPolicyController {
    
    private final UrgencyPolicyService policyService;
    
    public UrgencyPolicyController(UrgencyPolicyService policyService) {
        this.policyService = policyService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UrgencyPolicy> createPolicy(@RequestBody UrgencyPolicy policy) {
        return ResponseEntity.ok(policyService.createPolicy(policy));
    }
    
    @GetMapping
    public ResponseEntity<List<UrgencyPolicy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UrgencyPolicy> getPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicy(id));
    }
}