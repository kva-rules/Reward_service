package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.domain.ContributionSummary;
import com.cognizant.Reward_service.dto.event.RewardEventDTO;
import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.exception.InvalidEventException;
import com.cognizant.Reward_service.repository.ContributionSummaryRepository;
import com.cognizant.Reward_service.repository.RewardTransactionRepository;
import com.cognizant.Reward_service.service.RewardEventProcessor;
import com.cognizant.Reward_service.service.RewardService;
import com.cognizant.Reward_service.service.RuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardEventProcessorImpl implements RewardEventProcessor {

    private final RewardService rewardService;
    private final RuleEngine ruleEngine;
    private final ContributionSummaryRepository contributionSummaryRepository;
    private final RewardTransactionRepository transactionRepository;

    @Override
    @Transactional
    public void processEvent(RewardEventDTO event) {
        log.info("Processing event: {} for user: {}", event.getEventType(), event.getUserId());

        if (event.getUserId() == null) {
            throw new InvalidEventException("User ID is required in event");
        }

        if (event.getEventType() == null) {
            throw new InvalidEventException("Event type is required");
        }

        if (event.getReferenceId() != null && 
            transactionRepository.existsByUserIdAndReferenceId(event.getUserId(), event.getReferenceId())) {
            log.warn("Duplicate event detected for user: {} referenceId: {}", 
                    event.getUserId(), event.getReferenceId());
            return;
        }

        int points = ruleEngine.calculatePoints(event.getEventType());

        RewardRequestDTO request = RewardRequestDTO.builder()
                .userId(event.getUserId())
                .points(points)
                .reason(event.getEventType())
                .referenceId(event.getReferenceId())
                .build();

        rewardService.addPoints(request);

        updateContributionSummary(event);

        log.info("Event processed successfully: {} points awarded to user: {}", 
                points, event.getUserId());
    }

    private void updateContributionSummary(RewardEventDTO event) {
        ContributionSummary summary = contributionSummaryRepository.findByUserId(event.getUserId())
                .orElseGet(() -> {
                    ContributionSummary newSummary = new ContributionSummary();
                    newSummary.setUserId(event.getUserId());
                    newSummary.setTicketsResolved(0);
                    newSummary.setSolutionsApproved(0);
                    newSummary.setArticlesCreated(0);
                    newSummary.setUpvotesReceived(0);
                    return newSummary;
                });

        switch (event.getEventType()) {
            case "TICKET_RESOLVED":
                summary.setTicketsResolved(summary.getTicketsResolved() + 1);
                break;
            case "SOLUTION_APPROVED":
                summary.setSolutionsApproved(summary.getSolutionsApproved() + 1);
                break;
            case "KNOWLEDGE_CREATED":
                summary.setArticlesCreated(summary.getArticlesCreated() + 1);
                break;
            case "SOLUTION_VOTED":
            case "KNOWLEDGE_RATED":
                summary.setUpvotesReceived(summary.getUpvotesReceived() + 1);
                break;
            default:
                log.warn("Unknown event type for contribution summary: {}", event.getEventType());
        }

        contributionSummaryRepository.save(summary);
    }
}
