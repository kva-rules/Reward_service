package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.dto.event.BadgeAwardedEvent;
import com.cognizant.Reward_service.dto.event.LeaderboardUpdatedEvent;
import com.cognizant.Reward_service.dto.event.RewardPointsAddedEvent;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.enums.badgeName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RewardEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.reward-points-added}")
    private String rewardPointsAddedTopic;

    @Value("${app.kafka.topics.reward-badge-awarded}")
    private String rewardBadgeAwardedTopic;

    @Value("${app.kafka.topics.leaderboard-updated}")
    private String leaderboardUpdatedTopic;

    public void publishPointsAdded(UUID userId, Integer pointsAdded, Integer totalPoints, 
                                    String reason, UUID referenceId) {
        RewardPointsAddedEvent event = RewardPointsAddedEvent.builder()
                .userId(userId)
                .pointsAdded(pointsAdded)
                .totalPoints(totalPoints)
                .reason(reason)
                .referenceId(referenceId)
                .timestamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send(rewardPointsAddedTopic, userId.toString(), event);
        log.info("Published reward.points.added event for user: {}", userId);
    }

    public void publishBadgeAwarded(UUID userId, UUID badgeId, badgeName badgeName) {
        BadgeAwardedEvent event = BadgeAwardedEvent.builder()
                .userId(userId)
                .badgeId(badgeId)
                .badgeName(badgeName)
                .timestamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send(rewardBadgeAwardedTopic, userId.toString(), event);
        log.info("Published reward.badge.awarded event for user: {} badge: {}", userId, badgeName);
    }

    public void publishLeaderboardUpdated(Period period, Integer totalEntries) {
        LeaderboardUpdatedEvent event = LeaderboardUpdatedEvent.builder()
                .period(period)
                .totalEntries(totalEntries)
                .timestamp(System.currentTimeMillis())
                .build();

        kafkaTemplate.send(leaderboardUpdatedTopic, period.name(), event);
        log.info("Published leaderboard.updated event for period: {}", period);
    }
}
