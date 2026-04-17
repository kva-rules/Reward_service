package com.cognizant.Reward_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDTO {
    
    private Long totalPointsAwarded;
    private Long totalBadgesAwarded;
    private Long totalUsers;
    private List<LeaderboardResponseDTO> topUsers;
}
