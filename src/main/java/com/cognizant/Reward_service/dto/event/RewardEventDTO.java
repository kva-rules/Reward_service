package com.cognizant.Reward_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardEventDTO {
    
    private String eventType;
    private UUID userId;
    private UUID referenceId;
    private String referenceType;
    private Long timestamp;
}
