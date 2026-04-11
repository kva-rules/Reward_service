package com.cognizant.Reward_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "user_badges")
@Data
public class UserBadges {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private UUID userId;

    @JoinColumn(name = "badgeId", referencedColumnName = "badgeId", nullable = false)
    private UUID badgeId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date earnedAt;
}
