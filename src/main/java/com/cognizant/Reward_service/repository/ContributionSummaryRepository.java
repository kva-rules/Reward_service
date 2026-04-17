package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.ContributionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContributionSummaryRepository extends JpaRepository<ContributionSummary, UUID> {
    
    Optional<ContributionSummary> findByUserId(UUID userId);
}
