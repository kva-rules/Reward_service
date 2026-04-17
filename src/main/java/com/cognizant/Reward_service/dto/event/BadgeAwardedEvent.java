package com.cognizant.Reward_service.dto.event;

import com.cognizant.Reward_service.enums.badgeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAwardedEvent {
    
    private UUID userId;
    private UUID badgeId;
    private badgeName badgeName;
    private Long timestamp;
}
