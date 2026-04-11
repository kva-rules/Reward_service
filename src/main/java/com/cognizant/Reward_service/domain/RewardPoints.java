package com.cognizant.Reward_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "RewardPoints")
@Data
public class RewardPoints {
    @Id
    @GeneratedValue
    @Column(name = "rewardId", updatable = false, nullable = false)
    private UUID rewardId;

    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int totalPoints;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date lastUpdated;
}



