package com.cognizant.Reward_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardEventDTO {

    private String eventType;
    private UUID userId;
    private UUID referenceId;
    private String referenceType;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long timestamp;
}
