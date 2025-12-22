package com.example.demo.util;

import com.example.demo.model.*;

import java.util.List;

public class TicketCategorizationEngine {

    public void categorize(
            Ticket ticket,
            List<Category> categories,
            List<CategorizationRule> rules,
            List<UrgencyPolicy> policies,
            List<CategorizationLog> logs) {

        String description = ticket.getDescription().toLowerCase();

        // Step 1: Apply categorization rules
        for (CategorizationRule rule : rules) {

            String keyword = rule.getKeyword().toLowerCase();
            boolean matched = false;

            if (rule.getMatchType().equalsIgnoreCase("EXACT")) {
                matched = description.equals(keyword);
            }
            else if (rule.getMatchType().equalsIgnoreCase("CONTAINS")) {
                matched = description.contains(keyword);
            }
            else if (rule.getMatchType().equalsIgnoreCase("REGEX")) {
                matched = description.matches(keyword);
            }

            if (matched) {
                ticket.setAssignedCategory(rule.getCategory());
                ticket.setUrgencyLevel(
                        rule.getCategory().getDefaultUrgency()
                );

                CategorizationLog log = new CategorizationLog(
                        ticket,
                        rule,
                        rule.getKeyword(),
                        rule.getCategory().getCategoryName(),
                        ticket.getUrgencyLevel()
                );

                logs.add(log);
                break;
            }
        }

        // Step 2: Apply urgency policies
        for (UrgencyPolicy policy : policies) {
            if (description.contains(policy.getKeyword().toLowerCase())) {
                ticket.setUrgencyLevel(policy.getUrgencyOverride());
            }
        }
    }
}