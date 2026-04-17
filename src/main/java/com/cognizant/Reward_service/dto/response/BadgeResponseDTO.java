package com.cognizant.Reward_service.dto.response;

import com.cognizant.Reward_service.enums.badgeName;
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
public class BadgeResponseDTO {
    
    private UUID badgeId;
    private badgeName badgeName;
    private String description;
    private Integer pointsRequired;
    private Date createdAt;
}
