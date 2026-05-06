package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.domain.ContributionSummary;
import com.cognizant.Reward_service.domain.RewardActivityLog;
import com.cognizant.Reward_service.domain.RewardPoints;
import com.cognizant.Reward_service.domain.RewardTransactions;
import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import com.cognizant.Reward_service.enums.referenceTypes;
import com.cognizant.Reward_service.exception.ResourceNotFoundException;
import com.cognizant.Reward_service.kafka.RewardEventProducer;
import com.cognizant.Reward_service.mapper.RewardMapper;
import com.cognizant.Reward_service.repository.ContributionSummaryRepository;
import com.cognizant.Reward_service.repository.RewardActivityLogRepository;
import com.cognizant.Reward_service.repository.RewardPointsRepository;
import com.cognizant.Reward_service.repository.RewardTransactionRepository;
import com.cognizant.Reward_service.service.BadgeService;
import com.cognizant.Reward_service.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService {

    private final RewardPointsRepository rewardPointsRepository;
    private final RewardTransactionRepository transactionRepository;
    private final ContributionSummaryRepository contributionSummaryRepository;
    private final RewardActivityLogRepository activityLogRepository;
    private final RewardMapper rewardMapper;
    private final RewardEventProducer eventProducer;
    private final BadgeService badgeService;

    @Override
    @Transactional
    public RewardResponseDTO addPoints(RewardRequestDTO request) {
        log.info("Adding {} points for user: {}", request.getPoints(), request.getUserId());

        RewardPoints rewardPoints = rewardPointsRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    RewardPoints newRewardPoints = new RewardPoints();
                    newRewardPoints.setUserId(request.getUserId());
                    newRewardPoints.setTotalPoints(0);
                    return newRewardPoints;
                });

        rewardPoints.setTotalPoints(rewardPoints.getTotalPoints() + request.getPoints());
        rewardPoints.setLastUpdated(new Date());
        rewardPointsRepository.save(rewardPoints);

        RewardTransactions transaction = new RewardTransactions();
        transaction.setUserId(request.getUserId());
        transaction.setPoints(request.getPoints());
        transaction.setReason(request.getReason());
        transaction.setReferenceId(request.getReferenceId() != null ? request.getReferenceId() : UUID.randomUUID());
        transaction.setReferenceType(mapReasonToReferenceType(request.getReason()));
        transactionRepository.save(transaction);

        RewardActivityLog activityLog = new RewardActivityLog();
        activityLog.setUserId(request.getUserId());
        activityLog.setAction("POINTS_ADDED: " + request.getReason());
        activityLog.setPoints(request.getPoints());
        activityLogRepository.save(activityLog);

        eventProducer.publishPointsAdded(
                request.getUserId(),
                request.getPoints(),
                rewardPoints.getTotalPoints(),
                request.getReason(),
                request.getReferenceId()
        );

        badgeService.checkAndAssignEligibleBadges(request.getUserId(), rewardPoints.getTotalPoints());

        log.info("Points added successfully. User {} now has {} total points", 
                request.getUserId(), rewardPoints.getTotalPoints());

        return RewardResponseDTO.builder()
                .userId(request.getUserId())
                .totalPoints(rewardPoints.getTotalPoints())
                .build();
    }

    @Override
    public RewardResponseDTO getUserPoints(UUID userId) {
        log.debug("Fetching points for user: {}", userId);

        // A user with no reward activity is a normal state, not a 404 — the gateway and
        // frontend hit this endpoint for every authenticated user (including freshly-registered
        // ones who have never earned points). Return a zero-balance response instead of throwing.
        int totalPoints = rewardPointsRepository.findByUserId(userId)
                .map(RewardPoints::getTotalPoints)
                .orElse(0);

        return RewardResponseDTO.builder()
                .userId(userId)
                .totalPoints(totalPoints)
                .build();
    }

    @Override
    public Page<TransactionResponseDTO> getTransactions(UUID userId, Pageable pageable) {
        log.debug("Fetching transactions for user: {}", userId);
        
        return transactionRepository.findByUserId(userId, pageable)
                .map(rewardMapper::toTransactionResponseDTO);
    }

    @Override
    public ContributionSummaryDTO getContributionSummary(UUID userId) {
        log.debug("Fetching contribution summary for user: {}", userId);
        
        ContributionSummary summary = contributionSummaryRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ContributionSummary newSummary = new ContributionSummary();
                    newSummary.setUserId(userId);
                    newSummary.setTicketsResolved(0);
                    newSummary.setSolutionsApproved(0);
                    newSummary.setArticlesCreated(0);
                    newSummary.setUpvotesReceived(0);
                    return contributionSummaryRepository.save(newSummary);
                });

        return rewardMapper.toContributionSummaryDTO(summary);
    }

    private referenceTypes mapReasonToReferenceType(String reason) {
        if (reason == null) return referenceTypes.TICKET_RESOLVED;
        
        String upperReason = reason.toUpperCase();
        if (upperReason.contains("TICKET")) return referenceTypes.TICKET_RESOLVED;
        if (upperReason.contains("SOLUTION") && upperReason.contains("APPROVED")) return referenceTypes.SOLUTION_APPROVED;
        if (upperReason.contains("KNOWLEDGE") || upperReason.contains("ARTICLE")) return referenceTypes.KNOWLEDGE_CREATED;
        if (upperReason.contains("UPVOTE") || upperReason.contains("VOTE")) return referenceTypes.SOLUTION_UPVOTED;
        if (upperReason.contains("RATED")) return referenceTypes.ARTICLE_RATED;
        
        return referenceTypes.TICKET_RESOLVED;
    }
}
