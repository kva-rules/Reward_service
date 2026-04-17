package com.cognizant.Reward_service.scheduler;

import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaderboardScheduler {

    private final LeaderboardService leaderboardService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyLeaderboard() {
        log.info("Starting scheduled monthly leaderboard generation");
        try {
            leaderboardService.generateLeaderboard(Period.MONTHLY);
            log.info("Monthly leaderboard generation completed successfully");
        } catch (Exception e) {
            log.error("Error generating monthly leaderboard", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void generateWeeklyLeaderboard() {
        log.info("Starting scheduled weekly leaderboard generation");
        try {
            leaderboardService.generateLeaderboard(Period.WEEKLY);
            log.info("Weekly leaderboard generation completed successfully");
        } catch (Exception e) {
            log.error("Error generating weekly leaderboard", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyLeaderboard() {
        log.info("Starting scheduled daily leaderboard generation");
        try {
            leaderboardService.generateLeaderboard(Period.DAILY);
            log.info("Daily leaderboard generation completed successfully");
        } catch (Exception e) {
            log.error("Error generating daily leaderboard", e);
        }
    }
}
