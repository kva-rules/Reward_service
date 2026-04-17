package com.cognizant.Reward_service.mapper;

import com.cognizant.Reward_service.domain.ContributionSummary;
import com.cognizant.Reward_service.domain.RewardTransactions;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RewardMapper {
    
    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "points", source = "points")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "createdAt", source = "createdAt")
    TransactionResponseDTO toTransactionResponseDTO(RewardTransactions transaction);
    
    @Mapping(target = "summaryId", source = "summaryId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "ticketsResolved", source = "ticketsResolved")
    @Mapping(target = "solutionsApproved", source = "solutionsApproved")
    @Mapping(target = "articlesCreated", source = "articlesCreated")
    @Mapping(target = "upvotesReceived", source = "upvotesReceived")
    @Mapping(target = "lastUpdated", source = "lastUpdated")
    ContributionSummaryDTO toContributionSummaryDTO(ContributionSummary summary);
}
