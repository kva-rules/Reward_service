package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.RewardPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, UUID> {
    
    Optional<RewardPoints> findByUserId(UUID userId);
    
    @Query("SELECT COALESCE(SUM(r.totalPoints), 0) FROM RewardPoints r")
    Long sumAllPoints();
    
    @Query("SELECT COUNT(DISTINCT r.userId) FROM RewardPoints r")
    Long countDistinctUsers();
}
