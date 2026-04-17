package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaderboardService {
    
    void generateLeaderboard(Period period);
    
    Page<LeaderboardResponseDTO> getLeaderboard(Period period, Pageable pageable);
    
    List<LeaderboardResponseDTO> getTopContributors(Period period, int limit);
}
