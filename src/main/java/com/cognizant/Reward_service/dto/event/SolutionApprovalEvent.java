package com.cognizant.Reward_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolutionApprovalEvent {
    private String eventType;
    private UUID solutionId;
    private String ticketId;
    private String title;
    private UUID createdBy;
    private String status;
    private String difficultyLevel;
    // The solution-service publishes this field as "contributorIds" (List<String>);
    // accept both names so the reward consumer can map it regardless.
    @JsonProperty("contributorIds")
    private List<String> contributorIdStrings;  // raw strings from solution-service event
    private List<UUID> contributors;            // legacy field (kept for backward compat)
}
