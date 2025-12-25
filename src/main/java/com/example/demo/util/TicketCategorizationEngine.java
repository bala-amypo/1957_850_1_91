package com.example.demo.util;

import com.example.demo.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TicketCategorizationEngine {

    public void categorize(
            Ticket ticket,
            List<Category> categories,
            List<CategorizationRule> rules,
            List<UrgencyPolicy> policies,
            List<CategorizationLog> logs) {

        CategorizationRule matchedRule = null;
        Category assignedCategory = null;
        String urgencyLevel = "LOW";
        String matchedKeyword = "";

        // ✅ FIX: create mutable copy before sorting
        List<CategorizationRule> sortableRules = new ArrayList<>(rules);

        // Higher priority first
        sortableRules.sort((r1, r2) -> r2.getPriority().compareTo(r1.getPriority()));

        // Find matching rule
        for (CategorizationRule rule : sortableRules) {
            if (matchesRule(ticket.getDescription(), rule)) {
                matchedRule = rule;
                assignedCategory = rule.getCategory();
                urgencyLevel = assignedCategory.getDefaultUrgency();
                matchedKeyword = rule.getKeyword();
                break;
            }
        }

        // Apply urgency override policies
        for (UrgencyPolicy policy : policies) {
            if (ticket.getDescription() != null &&
                ticket.getDescription().toLowerCase()
                        .contains(policy.getKeyword().toLowerCase())) {
                urgencyLevel = policy.getUrgencyOverride();
                break;
            }
        }

        // Set ticket properties
        ticket.setAssignedCategory(assignedCategory);
        ticket.setUrgencyLevel(urgencyLevel);

        // Create log entry if rule matched
        if (matchedRule != null) {
            CategorizationLog log = new CategorizationLog(
                    ticket,
                    matchedRule,
                    matchedKeyword,
                    assignedCategory.getCategoryName(),
                    urgencyLevel
            );
            logs.add(log);
        }
    }

    private boolean matchesRule(String description, CategorizationRule rule) {
        if (description == null || rule.getKeyword() == null) {
            return false;
        }

        String keyword = rule.getKeyword();
        String matchType = rule.getMatchType();

        switch (matchType.toUpperCase()) {
            case "EXACT":
                return description.equals(keyword);
            case "CONTAINS":
                return description.toLowerCase().contains(keyword.toLowerCase());
            case "REGEX":
                return Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)
                        .matcher(description)
                        .find();
            default:
                return false;
        }
    }
}
