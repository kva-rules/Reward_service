package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.domain.ContributionSummary;
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
import com.cognizant.Reward_service.service.impl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardPointsRepository rewardPointsRepository;

    @Mock
    private RewardTransactionRepository transactionRepository;

    @Mock
    private ContributionSummaryRepository contributionSummaryRepository;

    @Mock
    private RewardActivityLogRepository activityLogRepository;

    @Mock
    private RewardMapper rewardMapper;

    @Mock
    private RewardEventProducer eventProducer;

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private UUID userId;
    private RewardPoints rewardPoints;
    private RewardRequestDTO rewardRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        rewardPoints = new RewardPoints();
        rewardPoints.setRewardId(UUID.randomUUID());
        rewardPoints.setUserId(userId);
        rewardPoints.setTotalPoints(100);
        rewardPoints.setLastUpdated(new Date());

        rewardRequest = RewardRequestDTO.builder()
                .userId(userId)
                .points(50)
                .reason("TICKET_RESOLVED")
                .referenceId(UUID.randomUUID())
                .build();
    }

    @Test
    void addPoints_NewUser_ShouldCreateRewardPoints() {
        when(rewardPointsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(rewardPointsRepository.save(any(RewardPoints.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(RewardTransactions.class))).thenAnswer(i -> i.getArgument(0));
        when(activityLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventProducer).publishPointsAdded(any(), any(), any(), any(), any());
        doNothing().when(badgeService).checkAndAssignEligibleBadges(any(), anyInt());

        RewardResponseDTO response = rewardService.addPoints(rewardRequest);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(50, response.getTotalPoints());
        verify(rewardPointsRepository, times(1)).save(any(RewardPoints.class));
        verify(transactionRepository, times(1)).save(any(RewardTransactions.class));
    }

    @Test
    void addPoints_ExistingUser_ShouldUpdatePoints() {
        when(rewardPointsRepository.findByUserId(userId)).thenReturn(Optional.of(rewardPoints));
        when(rewardPointsRepository.save(any(RewardPoints.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(RewardTransactions.class))).thenAnswer(i -> i.getArgument(0));
        when(activityLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventProducer).publishPointsAdded(any(), any(), any(), any(), any());
        doNothing().when(badgeService).checkAndAssignEligibleBadges(any(), anyInt());

        RewardResponseDTO response = rewardService.addPoints(rewardRequest);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(150, response.getTotalPoints());
    }

    @Test
    void getUserPoints_ExistingUser_ShouldReturnPoints() {
        when(rewardPointsRepository.findByUserId(userId)).thenReturn(Optional.of(rewardPoints));

        RewardResponseDTO response = rewardService.getUserPoints(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(100, response.getTotalPoints());
    }

    @Test
    void getUserPoints_NonExistingUser_ShouldThrowException() {
        when(rewardPointsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rewardService.getUserPoints(userId));
    }

    @Test
    void getTransactions_ShouldReturnPaginatedTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        RewardTransactions transaction = new RewardTransactions();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setUserId(userId);
        transaction.setPoints(50);
        transaction.setReason("TICKET_RESOLVED");
        transaction.setReferenceType(referenceTypes.TICKET_RESOLVED);
        transaction.setCreatedAt(new Date());

        Page<RewardTransactions> transactionPage = new PageImpl<>(List.of(transaction));
        
        TransactionResponseDTO dto = TransactionResponseDTO.builder()
                .transactionId(transaction.getTransactionId())
                .userId(userId)
                .points(50)
                .reason("TICKET_RESOLVED")
                .build();

        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(transactionPage);
        when(rewardMapper.toTransactionResponseDTO(any())).thenReturn(dto);

        Page<TransactionResponseDTO> response = rewardService.getTransactions(userId, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getContributionSummary_ExistingUser_ShouldReturnSummary() {
        ContributionSummary summary = new ContributionSummary();
        summary.setSummaryId(UUID.randomUUID());
        summary.setUserId(userId);
        summary.setTicketsResolved(10);
        summary.setSolutionsApproved(5);
        summary.setArticlesCreated(3);
        summary.setUpvotesReceived(20);

        ContributionSummaryDTO dto = ContributionSummaryDTO.builder()
                .summaryId(summary.getSummaryId())
                .userId(userId)
                .ticketsResolved(10)
                .solutionsApproved(5)
                .articlesCreated(3)
                .upvotesReceived(20)
                .build();

        when(contributionSummaryRepository.findByUserId(userId)).thenReturn(Optional.of(summary));
        when(rewardMapper.toContributionSummaryDTO(summary)).thenReturn(dto);

        ContributionSummaryDTO response = rewardService.getContributionSummary(userId);

        assertNotNull(response);
        assertEquals(10, response.getTicketsResolved());
        assertEquals(5, response.getSolutionsApproved());
    }
}
