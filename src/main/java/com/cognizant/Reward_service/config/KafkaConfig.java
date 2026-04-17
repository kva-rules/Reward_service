package com.cognizant.Reward_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topics.reward-points-added}")
    private String rewardPointsAddedTopic;

    @Value("${app.kafka.topics.reward-badge-awarded}")
    private String rewardBadgeAwardedTopic;

    @Value("${app.kafka.topics.leaderboard-updated}")
    private String leaderboardUpdatedTopic;

    @Bean
    public NewTopic rewardPointsAddedTopic() {
        return TopicBuilder.name(rewardPointsAddedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic rewardBadgeAwardedTopic() {
        return TopicBuilder.name(rewardBadgeAwardedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic leaderboardUpdatedTopic() {
        return TopicBuilder.name(leaderboardUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
