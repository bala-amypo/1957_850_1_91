package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categorization_logs")
public class CategorizationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private CategorizationRule appliedRule;

    @Column(nullable = false)
    private String matchedKeyword;

    @Column(nullable = false)
    private String assignedCategory;

    @Column(nullable = false)
    private String assignedUrgency;

    private LocalDateTime loggedAt;

    public CategorizationLog() {
    }

    public CategorizationLog(Ticket ticket, CategorizationRule appliedRule,
                             String matchedKeyword, String assignedCategory,
                             String assignedUrgency) {
        this.ticket = ticket;
        this.appliedRule = appliedRule;
        this.matchedKeyword = matchedKeyword;
        this.assignedCategory = assignedCategory;
        this.assignedUrgency = assignedUrgency;
    }

    @PrePersist
    public void onCreate() {
        this.loggedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public CategorizationRule getAppliedRule() {
        return appliedRule;
    }

    public String getMatchedKeyword() {
        return matchedKeyword;
    }

    public String getAssignedCategory() {
        return assignedCategory;
    }

    public String getAssignedUrgency() {
        return assignedUrgency;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }
}