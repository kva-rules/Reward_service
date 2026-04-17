package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.LeaderBoard;
import com.cognizant.Reward_service.enums.Period;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderBoard, UUID> {
    
    @Query("SELECT l FROM leaderBoard l WHERE l.period = :period ORDER BY l.rank ASC")
    List<LeaderBoard> findTopByPeriod(@Param("period") Period period);
    
    Page<LeaderBoard> findByPeriodOrderByRankAsc(Period period, Pageable pageable);
    
    Optional<LeaderBoard> findByUserIdAndPeriod(UUID userId, Period period);
    
    void deleteByPeriod(Period period);
    
    @Query("SELECT l FROM leaderBoard l WHERE l.period = :period ORDER BY l.rank ASC")
    List<LeaderBoard> findTopContributorsByPeriod(@Param("period") Period period, Pageable pageable);
}
