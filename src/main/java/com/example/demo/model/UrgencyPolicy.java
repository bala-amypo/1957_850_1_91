package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "urgency_policies")
public class UrgencyPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String policyName;
    
    @Column(nullable = false)
    private String keyword;
    
    @Column(nullable = false)
    private String urgencyOverride;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToMany(mappedBy = "urgencyPolicies")
    private Set<Category> categories = new HashSet<>();
    
    public UrgencyPolicy() {}
    
    public UrgencyPolicy(String policyName, String keyword, String urgencyOverride) {
        this.policyName = policyName;
        this.keyword = keyword;
        this.urgencyOverride = urgencyOverride;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void prePersist() {
        onCreate();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getUrgencyOverride() { return urgencyOverride; }
    public void setUrgencyOverride(String urgencyOverride) { this.urgencyOverride = urgencyOverride; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
}