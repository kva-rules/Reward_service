package com.cognizant.Reward_service.repository;

import com.cognizant.Reward_service.domain.RewardTransactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardTransactionRepository extends JpaRepository<RewardTransactions, UUID> {
    
    List<RewardTransactions> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Page<RewardTransactions> findByUserId(UUID userId, Pageable pageable);
    
    boolean existsByUserIdAndReferenceId(UUID userId, UUID referenceId);
}
