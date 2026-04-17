package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.dto.response.StatisticsResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.repository.RewardPointsRepository;
import com.cognizant.Reward_service.repository.UserBadgeRepository;
import com.cognizant.Reward_service.service.LeaderboardService;
import com.cognizant.Reward_service.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final RewardPointsRepository rewardPointsRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final LeaderboardService leaderboardService;

    @Override
    public StatisticsResponseDTO getStatistics() {
        log.debug("Fetching reward statistics");

        Long totalPoints = rewardPointsRepository.sumAllPoints();
        Long totalBadges = userBadgeRepository.countAllBadgesAwarded();
        Long totalUsers = rewardPointsRepository.countDistinctUsers();
        List<LeaderboardResponseDTO> topUsers = leaderboardService.getTopContributors(Period.ALL_TIME, 10);

        return StatisticsResponseDTO.builder()
                .totalPointsAwarded(totalPoints)
                .totalBadgesAwarded(totalBadges)
                .totalUsers(totalUsers)
                .topUsers(topUsers)
                .build();
    }
}
