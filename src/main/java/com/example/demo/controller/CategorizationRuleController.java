package com.example.demo.controller;

import com.example.demo.model.CategorizationRule;
import com.example.demo.service.CategorizationRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
@Tag(name = "Rule", description = "Categorization rule management endpoints")
public class CategorizationRuleController {
    
    private final CategorizationRuleService ruleService;
    
    public CategorizationRuleController(CategorizationRuleService ruleService) {
        this.ruleService = ruleService;
    }
    
    @PostMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategorizationRule> createRule(@PathVariable Long categoryId, 
                                                       @RequestBody CategorizationRule rule) {
        return ResponseEntity.ok(ruleService.createRule(categoryId, rule));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategorizationRule>> getRulesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ruleService.getRulesByCategory(categoryId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategorizationRule> getRule(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRule(id));
    }
}