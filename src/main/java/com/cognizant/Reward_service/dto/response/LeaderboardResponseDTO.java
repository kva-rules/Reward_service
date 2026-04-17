package com.cognizant.Reward_service.dto.response;

import com.cognizant.Reward_service.enums.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponseDTO {
    
    private UUID leaderboardId;
    private UUID userId;
    private Integer points;
    private Integer rank;
    private Period period;
    private Date generatedAt;
}
