package com.cognizant.Reward_service.domain;
import com.cognizant.Reward_service.enums.badgeName;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "Badges")
@Data
public class Badges {
    @Id
    @GeneratedValue
    @Column(name = "badgeId", updatable = false, nullable = false)
    private UUID badgeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private badgeName badgeName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private int pointsRequired;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
