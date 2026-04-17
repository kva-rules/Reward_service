package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.domain.RewardTransactions;
import com.cognizant.Reward_service.enums.referenceTypes;
import com.cognizant.Reward_service.repository.RewardTransactionRepository;
import com.library.common.event.RewardAddedEvent;
import com.library.common.event.SolutionApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolutionApprovedConsumer {

    private final RewardTransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REWARD_ADDED_TOPIC = "reward.added";

    @KafkaListener(topics = "solution.approved", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSolutionApproved(SolutionApprovedEvent event) {
        log.info("Received solution.approved event for solution: {}", event.getSolutionId());
        try {
            int pointsPerContributor = calculatePoints(event.getDifficulty());
            
            if (event.getContributorIds() != null && !event.getContributorIds().isEmpty()) {
                int pointsEach = pointsPerContributor / event.getContributorIds().size();
                
                for (Long contributorId : event.getContributorIds()) {
                    RewardTransactions transaction = new RewardTransactions();
                    transaction.setUserId(new UUID(0, contributorId));
                    transaction.setPoints(pointsEach);
                    transaction.setReason("Solution approved for ticket #" + event.getTicketId());
                    transaction.setReferenceId(new UUID(0, event.getSolutionId()));
                    transaction.setReferenceType(referenceTypes.SOLUTION_APPROVED);
                    
                    transactionRepository.save(transaction);
                    log.info("Created reward transaction for user {}: {} points", contributorId, pointsEach);
                    
                    // Publish reward.added event
                    RewardAddedEvent rewardEvent = RewardAddedEvent.builder()
                            .userId(contributorId)
                            .points(pointsEach)
                            .reason("Solution approved")
                            .build();
                    kafkaTemplate.send(REWARD_ADDED_TOPIC, rewardEvent);
                    log.info("Published reward.added event for user: {}", contributorId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing solution.approved event: {}", e.getMessage(), e);
        }
    }

    private int calculatePoints(String difficulty) {
        if (difficulty == null) {
            return 25; // Default to MEDIUM
        }
        return switch (difficulty.toUpperCase()) {
            case "EASY" -> 10;
            case "MEDIUM" -> 25;
            case "HARD" -> 50;
            case "CRITICAL" -> 100;
            default -> 25;
        };
    }
}
