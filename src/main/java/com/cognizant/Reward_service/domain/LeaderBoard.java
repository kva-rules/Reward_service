package com.cognizant.Reward_service.domain;
import com.cognizant.Reward_service.enums.Period;
import jakarta.persistence.*;
import lombok.Data;
import com.cognizant.Reward_service.enums.Period;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import java.util.UUID;

@Entity(name = "leaderBoard")
@Data
public class LeaderBoard {

    @Id
    @GeneratedValue
    @Column(name = "leaderboardId", updatable = false, nullable = false)
    private UUID leaderboardId;

    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private int rank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Period period;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date generatedAt;


}


