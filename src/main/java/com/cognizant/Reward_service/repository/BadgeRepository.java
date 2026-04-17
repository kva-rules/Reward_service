package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.Badges;
import com.cognizant.Reward_service.enums.badgeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badges, UUID> {
    
    Optional<Badges> findByBadgeName(badgeName badgeName);
    
    @Query("SELECT b FROM Badges b WHERE b.pointsRequired <= :points ORDER BY b.pointsRequired DESC")
    List<Badges> findEligibleBadges(@Param("points") int points);
    
    boolean existsByBadgeName(badgeName badgeName);
}
