package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.service.impl.RuleEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleEngineTest {

    private RuleEngineImpl ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngineImpl(50, 30, 20, 5);
    }

    @Test
    void calculatePoints_TicketResolved_ShouldReturn50() {
        int points = ruleEngine.calculatePoints("TICKET_RESOLVED");
        assertEquals(50, points);
    }

    @Test
    void calculatePoints_SolutionApproved_ShouldReturn30() {
        int points = ruleEngine.calculatePoints("SOLUTION_APPROVED");
        assertEquals(30, points);
    }

    @Test
    void calculatePoints_KnowledgeCreated_ShouldReturn20() {
        int points = ruleEngine.calculatePoints("KNOWLEDGE_CREATED");
        assertEquals(20, points);
    }

    @Test
    void calculatePoints_SolutionVoted_ShouldReturn5() {
        int points = ruleEngine.calculatePoints("SOLUTION_VOTED");
        assertEquals(5, points);
    }

    @Test
    void calculatePoints_KnowledgeRated_ShouldReturn5() {
        int points = ruleEngine.calculatePoints("KNOWLEDGE_RATED");
        assertEquals(5, points);
    }

    @Test
    void calculatePoints_UnknownEventType_ShouldReturn0() {
        int points = ruleEngine.calculatePoints("UNKNOWN_EVENT");
        assertEquals(0, points);
    }

    @Test
    void calculatePoints_NullEventType_ShouldReturn0() {
        int points = ruleEngine.calculatePoints(null);
        assertEquals(0, points);
    }

    @Test
    void calculatePoints_WithDifferentCasing_ShouldNormalizeAndReturn() {
        int points = ruleEngine.calculatePoints("ticket_resolved");
        assertEquals(50, points);
    }

    @Test
    void getAllRules_ShouldReturnAllConfiguredRules() {
        var rules = ruleEngine.getAllRules();
        
        assertNotNull(rules);
        assertEquals(6, rules.size());
        assertEquals(50, rules.get("TICKET_RESOLVED"));
        assertEquals(30, rules.get("SOLUTION_APPROVED"));
        assertEquals(20, rules.get("KNOWLEDGE_CREATED"));
    }

    @Test
    void updateRule_ShouldUpdateExistingRule() {
        ruleEngine.updateRule("TICKET_RESOLVED", 100);
        
        int points = ruleEngine.calculatePoints("TICKET_RESOLVED");
        assertEquals(100, points);
    }
}
