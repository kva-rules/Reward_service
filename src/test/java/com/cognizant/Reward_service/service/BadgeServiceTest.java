package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.domain.Badges;
import com.cognizant.Reward_service.domain.UserBadges;
import com.cognizant.Reward_service.dto.request.BadgeAssignRequestDTO;
import com.cognizant.Reward_service.dto.request.BadgeRequestDTO;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import com.cognizant.Reward_service.dto.response.UserBadgeResponseDTO;
import com.cognizant.Reward_service.enums.badgeName;
import com.cognizant.Reward_service.exception.DuplicateBadgeException;
import com.cognizant.Reward_service.exception.ResourceNotFoundException;
import com.cognizant.Reward_service.kafka.RewardEventProducer;
import com.cognizant.Reward_service.mapper.BadgeMapper;
import com.cognizant.Reward_service.repository.BadgeRepository;
import com.cognizant.Reward_service.repository.UserBadgeRepository;
import com.cognizant.Reward_service.service.impl.BadgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private BadgeMapper badgeMapper;

    @Mock
    private RewardEventProducer eventProducer;

    @InjectMocks
    private BadgeServiceImpl badgeService;

    private UUID userId;
    private UUID badgeId;
    private Badges badge;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        badgeId = UUID.randomUUID();

        badge = new Badges();
        badge.setBadgeId(badgeId);
        badge.setBadgeName(badgeName.FIRST_SOLVER);
        badge.setDescription("First solution approved");
        badge.setPointsRequired(100);
        badge.setCreatedAt(new Date());
    }

    @Test
    void createBadge_NewBadge_ShouldCreateSuccessfully() {
        BadgeRequestDTO request = BadgeRequestDTO.builder()
                .badgeName(badgeName.FIRST_SOLVER)
                .description("First solution approved")
                .pointsRequired(100)
                .build();

        BadgeResponseDTO responseDTO = BadgeResponseDTO.builder()
                .badgeId(badgeId)
                .badgeName(badgeName.FIRST_SOLVER)
                .description("First solution approved")
                .pointsRequired(100)
                .build();

        when(badgeRepository.existsByBadgeName(badgeName.FIRST_SOLVER)).thenReturn(false);
        when(badgeRepository.save(any(Badges.class))).thenReturn(badge);
        when(badgeMapper.toBadgeResponseDTO(badge)).thenReturn(responseDTO);

        BadgeResponseDTO response = badgeService.createBadge(request);

        assertNotNull(response);
        assertEquals(badgeName.FIRST_SOLVER, response.getBadgeName());
        verify(badgeRepository, times(1)).save(any(Badges.class));
    }

    @Test
    void createBadge_DuplicateBadge_ShouldThrowException() {
        BadgeRequestDTO request = BadgeRequestDTO.builder()
                .badgeName(badgeName.FIRST_SOLVER)
                .description("First solution approved")
                .pointsRequired(100)
                .build();

        when(badgeRepository.existsByBadgeName(badgeName.FIRST_SOLVER)).thenReturn(true);

        assertThrows(DuplicateBadgeException.class, () -> badgeService.createBadge(request));
    }

    @Test
    void assignBadge_ValidRequest_ShouldAssignSuccessfully() {
        BadgeAssignRequestDTO request = BadgeAssignRequestDTO.builder()
                .userId(userId)
                .badgeId(badgeId)
                .build();

        UserBadges userBadge = new UserBadges();
        userBadge.setId(UUID.randomUUID());
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badgeId);
        userBadge.setEarnedAt(new Date());

        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId)).thenReturn(false);
        when(badgeRepository.findById(badgeId)).thenReturn(Optional.of(badge));
        when(userBadgeRepository.save(any(UserBadges.class))).thenReturn(userBadge);
        doNothing().when(eventProducer).publishBadgeAwarded(any(), any(), any());

        UserBadgeResponseDTO response = badgeService.assignBadge(request);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(badgeId, response.getBadgeId());
    }

    @Test
    void assignBadge_DuplicateAssignment_ShouldThrowException() {
        BadgeAssignRequestDTO request = BadgeAssignRequestDTO.builder()
                .userId(userId)
                .badgeId(badgeId)
                .build();

        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId)).thenReturn(true);

        assertThrows(DuplicateBadgeException.class, () -> badgeService.assignBadge(request));
    }

    @Test
    void assignBadge_BadgeNotFound_ShouldThrowException() {
        BadgeAssignRequestDTO request = BadgeAssignRequestDTO.builder()
                .userId(userId)
                .badgeId(badgeId)
                .build();

        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId)).thenReturn(false);
        when(badgeRepository.findById(badgeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> badgeService.assignBadge(request));
    }

    @Test
    void getUserBadges_ShouldReturnUserBadges() {
        UserBadges userBadge = new UserBadges();
        userBadge.setId(UUID.randomUUID());
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badgeId);
        userBadge.setEarnedAt(new Date());

        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of(userBadge));
        when(badgeRepository.findById(badgeId)).thenReturn(Optional.of(badge));

        List<UserBadgeResponseDTO> response = badgeService.getUserBadges(userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(badgeName.FIRST_SOLVER, response.get(0).getBadgeName());
    }

    @Test
    void checkAndAssignEligibleBadges_ShouldAssignEligibleBadges() {
        when(badgeRepository.findEligibleBadges(150)).thenReturn(List.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId)).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadges.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventProducer).publishBadgeAwarded(any(), any(), any());

        badgeService.checkAndAssignEligibleBadges(userId, 150);

        verify(userBadgeRepository, times(1)).save(any(UserBadges.class));
        verify(eventProducer, times(1)).publishBadgeAwarded(userId, badgeId, badgeName.FIRST_SOLVER);
    }
}
