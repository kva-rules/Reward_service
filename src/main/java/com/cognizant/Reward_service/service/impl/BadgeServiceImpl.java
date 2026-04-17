package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.domain.Badges;
import com.cognizant.Reward_service.domain.UserBadges;
import com.cognizant.Reward_service.dto.request.BadgeAssignRequestDTO;
import com.cognizant.Reward_service.dto.request.BadgeRequestDTO;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import com.cognizant.Reward_service.dto.response.UserBadgeResponseDTO;
import com.cognizant.Reward_service.exception.DuplicateBadgeException;
import com.cognizant.Reward_service.exception.ResourceNotFoundException;
import com.cognizant.Reward_service.kafka.RewardEventProducer;
import com.cognizant.Reward_service.mapper.BadgeMapper;
import com.cognizant.Reward_service.repository.BadgeRepository;
import com.cognizant.Reward_service.repository.UserBadgeRepository;
import com.cognizant.Reward_service.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeMapper badgeMapper;
    private final RewardEventProducer eventProducer;

    @Override
    @Transactional
    public BadgeResponseDTO createBadge(BadgeRequestDTO request) {
        log.info("Creating badge: {}", request.getBadgeName());

        if (badgeRepository.existsByBadgeName(request.getBadgeName())) {
            throw new DuplicateBadgeException("Badge already exists: " + request.getBadgeName());
        }

        Badges badge = new Badges();
        badge.setBadgeName(request.getBadgeName());
        badge.setDescription(request.getDescription());
        badge.setPointsRequired(request.getPointsRequired());

        badge = badgeRepository.save(badge);
        log.info("Badge created successfully: {}", badge.getBadgeId());

        return badgeMapper.toBadgeResponseDTO(badge);
    }

    @Override
    @Transactional
    public UserBadgeResponseDTO assignBadge(BadgeAssignRequestDTO request) {
        log.info("Assigning badge {} to user {}", request.getBadgeId(), request.getUserId());

        if (userBadgeRepository.existsByUserIdAndBadgeId(request.getUserId(), request.getBadgeId())) {
            throw new DuplicateBadgeException("User already has this badge");
        }

        Badges badge = badgeRepository.findById(request.getBadgeId())
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found: " + request.getBadgeId()));

        UserBadges userBadge = new UserBadges();
        userBadge.setUserId(request.getUserId());
        userBadge.setBadgeId(request.getBadgeId());

        userBadge = userBadgeRepository.save(userBadge);

        eventProducer.publishBadgeAwarded(request.getUserId(), badge.getBadgeId(), badge.getBadgeName());

        log.info("Badge {} assigned to user {} successfully", badge.getBadgeName(), request.getUserId());

        return UserBadgeResponseDTO.builder()
                .id(userBadge.getId())
                .userId(userBadge.getUserId())
                .badgeId(userBadge.getBadgeId())
                .badgeName(badge.getBadgeName())
                .badgeDescription(badge.getDescription())
                .earnedAt(userBadge.getEarnedAt())
                .build();
    }

    @Override
    public List<UserBadgeResponseDTO> getUserBadges(UUID userId) {
        log.debug("Fetching badges for user: {}", userId);

        List<UserBadges> userBadges = userBadgeRepository.findByUserId(userId);

        return userBadges.stream()
                .map(ub -> {
                    Badges badge = badgeRepository.findById(ub.getBadgeId())
                            .orElse(null);
                    return UserBadgeResponseDTO.builder()
                            .id(ub.getId())
                            .userId(ub.getUserId())
                            .badgeId(ub.getBadgeId())
                            .badgeName(badge != null ? badge.getBadgeName() : null)
                            .badgeDescription(badge != null ? badge.getDescription() : null)
                            .earnedAt(ub.getEarnedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkAndAssignEligibleBadges(UUID userId, int totalPoints) {
        log.debug("Checking badge eligibility for user {} with {} points", userId, totalPoints);

        List<Badges> eligibleBadges = badgeRepository.findEligibleBadges(totalPoints);

        for (Badges badge : eligibleBadges) {
            if (!userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getBadgeId())) {
                UserBadges userBadge = new UserBadges();
                userBadge.setUserId(userId);
                userBadge.setBadgeId(badge.getBadgeId());
                userBadgeRepository.save(userBadge);

                eventProducer.publishBadgeAwarded(userId, badge.getBadgeId(), badge.getBadgeName());

                log.info("Badge {} automatically assigned to user {}", badge.getBadgeName(), userId);
            }
        }
    }

    @Override
    public List<BadgeResponseDTO> getAllBadges() {
        log.debug("Fetching all badges");
        return badgeRepository.findAll().stream()
                .map(badgeMapper::toBadgeResponseDTO)
                .collect(Collectors.toList());
    }
}
