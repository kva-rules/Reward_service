package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.RewardActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardActivityLogRepository extends JpaRepository<RewardActivityLog, UUID> {
    
    List<RewardActivityLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Page<RewardActivityLog> findByUserId(UUID userId, Pageable pageable);
}
