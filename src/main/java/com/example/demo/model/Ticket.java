package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String description;
    
    private String location;
    
    private String createdBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_category_id")
    private Category assignedCategory;
    
    private String urgencyLevel;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public Ticket() {}
    
    public Ticket(String title, String description, String location, String createdBy) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.createdBy = createdBy;
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
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Category getAssignedCategory() { return assignedCategory; }
    public void setAssignedCategory(Category assignedCategory) { this.assignedCategory = assignedCategory; }
    
    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}