package com.cognizant.Reward_service.service.impl;

import com.cognizant.Reward_service.domain.LeaderBoard;
import com.cognizant.Reward_service.domain.RewardPoints;
import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.kafka.RewardEventProducer;
import com.cognizant.Reward_service.mapper.LeaderboardMapper;
import com.cognizant.Reward_service.repository.LeaderboardRepository;
import com.cognizant.Reward_service.repository.RewardPointsRepository;
import com.cognizant.Reward_service.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final RewardPointsRepository rewardPointsRepository;
    private final LeaderboardMapper leaderboardMapper;
    private final RewardEventProducer eventProducer;

    @Override
    @Transactional
    public void generateLeaderboard(Period period) {
        log.info("Generating leaderboard for period: {}", period);

        leaderboardRepository.deleteByPeriod(period);

        List<RewardPoints> allRewardPoints = rewardPointsRepository.findAll();

        List<RewardPoints> sortedPoints = allRewardPoints.stream()
                .sorted(Comparator.comparingInt(RewardPoints::getTotalPoints).reversed())
                .collect(Collectors.toList());

        AtomicInteger rank = new AtomicInteger(1);
        
        List<LeaderBoard> leaderboardEntries = sortedPoints.stream()
                .map(rp -> {
                    LeaderBoard entry = new LeaderBoard();
                    entry.setUserId(rp.getUserId());
                    entry.setPoints(rp.getTotalPoints());
                    entry.setRank(rank.getAndIncrement());
                    entry.setPeriod(period);
                    return entry;
                })
                .collect(Collectors.toList());

        leaderboardRepository.saveAll(leaderboardEntries);

        eventProducer.publishLeaderboardUpdated(period, leaderboardEntries.size());

        log.info("Leaderboard generated successfully with {} entries for period: {}", 
                leaderboardEntries.size(), period);
    }

    @Override
    public Page<LeaderboardResponseDTO> getLeaderboard(Period period, Pageable pageable) {
        log.debug("Fetching leaderboard for period: {}", period);
        
        return leaderboardRepository.findByPeriodOrderByRankAsc(period, pageable)
                .map(leaderboardMapper::toLeaderboardResponseDTO);
    }

    @Override
    public List<LeaderboardResponseDTO> getTopContributors(Period period, int limit) {
        log.debug("Fetching top {} contributors for period: {}", limit, period);
        
        return leaderboardRepository.findTopContributorsByPeriod(period, PageRequest.of(0, limit))
                .stream()
                .map(leaderboardMapper::toLeaderboardResponseDTO)
                .collect(Collectors.toList());
    }
}
