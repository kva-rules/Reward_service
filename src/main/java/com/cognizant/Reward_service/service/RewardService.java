package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RewardService {
    
    RewardResponseDTO addPoints(RewardRequestDTO request);
    
    RewardResponseDTO getUserPoints(UUID userId);
    
    Page<TransactionResponseDTO> getTransactions(UUID userId, Pageable pageable);
    
    ContributionSummaryDTO getContributionSummary(UUID userId);
}
