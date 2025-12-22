package com.example.demo.repository;

import com.example.demo.model.UrgencyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrgencyPolicyRepository
        extends JpaRepository<UrgencyPolicy, Long> {

    List<UrgencyPolicy> findByKeywordContainingIgnoreCase(String keyword);
}