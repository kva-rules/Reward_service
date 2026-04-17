package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.service.RuleEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RuleEngineImpl implements RuleEngine {

    private final Map<String, Integer> pointRules = new HashMap<>();

    public RuleEngineImpl(
            @Value("${app.reward.rules.ticket-resolved:50}") int ticketResolvedPoints,
            @Value("${app.reward.rules.solution-approved:30}") int solutionApprovedPoints,
            @Value("${app.reward.rules.knowledge-created:20}") int knowledgeCreatedPoints,
            @Value("${app.reward.rules.upvote-received:5}") int upvoteReceivedPoints
    ) {
        pointRules.put("TICKET_RESOLVED", ticketResolvedPoints);
        pointRules.put("SOLUTION_APPROVED", solutionApprovedPoints);
        pointRules.put("KNOWLEDGE_CREATED", knowledgeCreatedPoints);
        pointRules.put("SOLUTION_VOTED", upvoteReceivedPoints);
        pointRules.put("KNOWLEDGE_RATED", upvoteReceivedPoints);
        pointRules.put("UPVOTE_RECEIVED", upvoteReceivedPoints);

        log.info("Rule Engine initialized with rules: {}", pointRules);
    }

    @Override
    public int calculatePoints(String eventType) {
        if (eventType == null) {
            log.warn("Null event type received, returning 0 points");
            return 0;
        }

        String normalizedEventType = eventType.toUpperCase().replace("-", "_").replace(".", "_");
        
        Integer points = pointRules.get(normalizedEventType);
        
        if (points == null) {
            log.warn("Unknown event type: {}, returning 0 points", eventType);
            return 0;
        }

        log.debug("Calculated {} points for event type: {}", points, eventType);
        return points;
    }

    public Map<String, Integer> getAllRules() {
        return new HashMap<>(pointRules);
    }

    public void updateRule(String eventType, int points) {
        String normalizedEventType = eventType.toUpperCase().replace("-", "_").replace(".", "_");
        pointRules.put(normalizedEventType, points);
        log.info("Updated rule: {} -> {} points", normalizedEventType, points);
    }
}
