package com.cognizant.Reward_service.dto.request;

import com.cognizant.Reward_service.enums.Period;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardGenerateRequestDTO {
    
    @NotNull(message = "Period is required")
    private Period period;
}
