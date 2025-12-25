package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.model.CategorizationRule;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.CategorizationRuleRepository;
import com.example.demo.service.CategorizationRuleService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategorizationRuleServiceImpl implements CategorizationRuleService {
    
    private final CategorizationRuleRepository ruleRepository;
    private final CategoryRepository categoryRepository;
    
    public CategorizationRuleServiceImpl(CategorizationRuleRepository ruleRepository, 
                                       CategoryRepository categoryRepository) {
        this.ruleRepository = ruleRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public CategorizationRule createRule(Long categoryId, CategorizationRule rule) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Rule not found"));
        rule.setCategory(category);
        return ruleRepository.save(rule);
    }
    
    @Override
    public List<CategorizationRule> getRulesByCategory(Long categoryId) {
        return ruleRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public CategorizationRule getRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }
}