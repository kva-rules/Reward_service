package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.dto.request.BadgeAssignRequestDTO;
import com.cognizant.Reward_service.dto.request.BadgeRequestDTO;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import com.cognizant.Reward_service.dto.response.UserBadgeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BadgeService {
    
    BadgeResponseDTO createBadge(BadgeRequestDTO request);
    
    UserBadgeResponseDTO assignBadge(BadgeAssignRequestDTO request);
    
    List<UserBadgeResponseDTO> getUserBadges(UUID userId);
    
    void checkAndAssignEligibleBadges(UUID userId, int totalPoints);
    
    List<BadgeResponseDTO> getAllBadges();
}
