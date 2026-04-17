package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.enums.badgeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private RewardEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new RewardEventProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "rewardPointsAddedTopic", "reward.points.added");
        ReflectionTestUtils.setField(producer, "rewardBadgeAwardedTopic", "reward.badge.awarded");
        ReflectionTestUtils.setField(producer, "leaderboardUpdatedTopic", "leaderboard.updated");
    }

    @Test
    void publishPointsAdded_ShouldSendMessage() {
        UUID userId = UUID.randomUUID();
        UUID referenceId = UUID.randomUUID();

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);

        producer.publishPointsAdded(userId, 50, 150, "TICKET_RESOLVED", referenceId);

        verify(kafkaTemplate, times(1)).send(eq("reward.points.added"), eq(userId.toString()), any());
    }

    @Test
    void publishBadgeAwarded_ShouldSendMessage() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);

        producer.publishBadgeAwarded(userId, badgeId, badgeName.FIRST_SOLVER);

        verify(kafkaTemplate, times(1)).send(eq("reward.badge.awarded"), eq(userId.toString()), any());
    }

    @Test
    void publishLeaderboardUpdated_ShouldSendMessage() {
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);

        producer.publishLeaderboardUpdated(Period.MONTHLY, 100);

        verify(kafkaTemplate, times(1)).send(eq("leaderboard.updated"), eq("MONTHLY"), any());
    }
}
