package com.cognizant.Reward_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "user_activities")
@Data
public class RewardActivityLog {
    @Id
    @GeneratedValue
    @Column(name = "activityId", updatable = false, nullable = false)
    private UUID activityId;

    @JoinColumn(name = "userId",  nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private int points;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}




