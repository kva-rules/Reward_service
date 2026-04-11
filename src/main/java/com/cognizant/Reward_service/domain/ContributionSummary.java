package com.cognizant.Reward_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "ContributionSummary")
@Data
public class ContributionSummary {

    @Id
    @GeneratedValue
    @Column(name = "summaryId", updatable = false, nullable = false)
    private UUID summaryId;

    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int ticketsResolved;

    @Column(nullable = false)
    private int solutionsApproved;

    @Column(nullable = false)
    private int articlesCreated;

    @Column(nullable = false)
    private int upvotesReceived;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date lastUpdated;

}
