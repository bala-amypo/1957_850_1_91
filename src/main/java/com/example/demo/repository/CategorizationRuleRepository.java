package com.example.demo.repository;

import com.example.demo.model.CategorizationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategorizationRuleRepository extends JpaRepository<CategorizationRule, Long> {
    List<CategorizationRule> findByCategoryId(Long categoryId);
    List<CategorizationRule> findByKeywordContainingIgnoreCase(String keyword);
}