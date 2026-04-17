package com.cognizant.Reward_service.dto.event;

import com.cognizant.Reward_service.enums.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardUpdatedEvent {
    
    private Period period;
    private Integer totalEntries;
    private Long timestamp;
}
