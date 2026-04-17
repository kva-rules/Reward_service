package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.dto.event.RewardEventDTO;
import com.cognizant.Reward_service.service.RewardEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(topics = "${app.kafka.topics.solution-approved}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSolutionApproved(RewardEventDTO event) {
        log.info("Received solution.approved event for user: {}", event.getUserId());
        event.setEventType("SOLUTION_APPROVED");
        eventProcessor.processEvent(event);
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
}
