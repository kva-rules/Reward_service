package com.cognizant.Reward_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequestDTO {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Points are required")
    @Positive(message = "Points must be positive")
    private Integer points;
    
    @NotNull(message = "Reason is required")
    private String reason;
    
    private UUID referenceId;
}
