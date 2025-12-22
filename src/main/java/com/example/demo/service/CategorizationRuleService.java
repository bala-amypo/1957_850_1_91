package com.example.demo.service;

import com.example.demo.model.CategorizationRule;

import java.util.List;

public interface CategorizationRuleService {

    CategorizationRule createRule(Long categoryId, CategorizationRule rule);

    CategorizationRule getRuleById(Long id);

    List<CategorizationRule> getRulesByCategory(Long categoryId);

    List<CategorizationRule> getAllRules();

    void deleteRule(Long id);
}