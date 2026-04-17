package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.UserBadges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadges, UUID> {
    
    List<UserBadges> findByUserId(UUID userId);
    
    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);
    
    @Query("SELECT COUNT(ub) FROM user_badges ub")
    Long countAllBadgesAwarded();
    
    long countByUserId(UUID userId);
}
