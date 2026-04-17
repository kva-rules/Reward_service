package com.cognizant.Reward_service.dto.response;

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
public class ContributionSummaryDTO {
    
    private UUID summaryId;
    private UUID userId;
    private Integer ticketsResolved;
    private Integer solutionsApproved;
    private Integer articlesCreated;
    private Integer upvotesReceived;
    private Date lastUpdated;
}
