package com.cognizant.Reward_service.domain;
import com.cognizant.Reward_service.enums.referenceTypes;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "reward_transactions")
@Data
public class RewardTransactions {
    @Id
    @GeneratedValue
    @Column(name = "transactionId", updatable = false, nullable = false)
    private UUID transactionId;

    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private UUID userId;
//Use Common library for connecting to user Table and column userId
    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private referenceTypes referenceType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}

