package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.dto.event.RewardEventDTO;
import com.cognizant.Reward_service.dto.event.SolutionApprovalEvent;
import com.cognizant.Reward_service.service.RewardEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RewardEventConsumer {

    private final RewardEventProcessor eventProcessor;

    @KafkaListener(topics = "${app.kafka.topics.ticket-resolved}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTicketResolved(RewardEventDTO event) {
        log.info("Received ticket.resolved event for user: {}", event.getUserId());
        event.setEventType("TICKET_RESOLVED");
        eventProcessor.processEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.solution-approved}",
                   groupId = "${spring.kafka.consumer.group-id}-rewards",
                   containerFactory = "solutionApprovedContainerFactory")
    public void consumeSolutionApproved(SolutionApprovalEvent event) {
        if (event == null) return;
        log.info("Received solution.approved event for solution: {}", event.getSolutionId());

        List<UUID> recipients = new java.util.ArrayList<>();
        // Prefer contributorIdStrings (mapped from "contributorIds" in the solution-service event)
        if (event.getContributorIdStrings() != null) {
            event.getContributorIdStrings().stream()
                    .map(this::parseUuid)
                    .filter(u -> u != null)
                    .forEach(recipients::add);
        }
        // Fallback: legacy "contributors" field (List<UUID>)
        if (recipients.isEmpty() && event.getContributors() != null) {
            recipients.addAll(event.getContributors());
        }
        if (event.getCreatedBy() != null && !recipients.contains(event.getCreatedBy())) {
            recipients.add(event.getCreatedBy());
        }

        if (recipients.isEmpty()) {
            log.warn("solution.approved event has no recipients, skipping reward");
            return;
        }
        for (UUID contributorUuid : recipients) {
            RewardEventDTO rewardEvent = RewardEventDTO.builder()
                    .eventType("SOLUTION_APPROVED")
                    .userId(contributorUuid)
                    .referenceId(event.getSolutionId())
                    .referenceType("SOLUTION")
                    .timestamp(System.currentTimeMillis())
                    .build();
            eventProcessor.processEvent(rewardEvent);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.knowledge-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeKnowledgeCreated(RewardEventDTO event) {
        log.info("Received knowledge.created event for user: {}", event.getUserId());
        event.setEventType("KNOWLEDGE_CREATED");
        eventProcessor.processEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.solution-voted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSolutionVoted(RewardEventDTO event) {
        log.info("Received solution.voted event for user: {}", event.getUserId());
        event.setEventType("SOLUTION_VOTED");
        eventProcessor.processEvent(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.knowledge-rated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeKnowledgeRated(RewardEventDTO event) {
        log.info("Received knowledge.rated event for user: {}", event.getUserId());
        event.setEventType("KNOWLEDGE_RATED");
        eventProcessor.processEvent(event);
    }

    private UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            log.warn("Could not parse UUID: {}", value);
            return null;
        }
    }
}
