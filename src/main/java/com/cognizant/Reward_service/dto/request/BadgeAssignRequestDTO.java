package com.cognizant.Reward_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAssignRequestDTO {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Badge ID is required")
    private UUID badgeId;
}
