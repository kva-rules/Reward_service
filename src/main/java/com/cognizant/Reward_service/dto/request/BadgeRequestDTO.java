package com.cognizant.Reward_service.dto.request;

import com.cognizant.Reward_service.enums.badgeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeRequestDTO {
    
    @NotNull(message = "Badge name is required")
    private badgeName badgeName;
    
    @NotNull(message = "Description is required")
    private String description;
    
    @NotNull(message = "Points required is mandatory")
    @Positive(message = "Points required must be positive")
    private Integer pointsRequired;
}
