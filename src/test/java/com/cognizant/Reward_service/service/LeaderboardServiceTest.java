package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.domain.LeaderBoard;
import com.cognizant.Reward_service.domain.RewardPoints;
import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.kafka.RewardEventProducer;
import com.cognizant.Reward_service.mapper.LeaderboardMapper;
import com.cognizant.Reward_service.repository.LeaderboardRepository;
import com.cognizant.Reward_service.repository.RewardPointsRepository;
import com.cognizant.Reward_service.service.impl.LeaderboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @Mock
    private RewardPointsRepository rewardPointsRepository;

    @Mock
    private LeaderboardMapper leaderboardMapper;

    @Mock
    private RewardEventProducer eventProducer;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
    }

    @Test
    void generateLeaderboard_ShouldCreateLeaderboardEntries() {
        RewardPoints rp1 = new RewardPoints();
        rp1.setUserId(userId1);
        rp1.setTotalPoints(100);

        RewardPoints rp2 = new RewardPoints();
        rp2.setUserId(userId2);
        rp2.setTotalPoints(200);

        when(rewardPointsRepository.findAll()).thenReturn(List.of(rp1, rp2));
        doNothing().when(leaderboardRepository).deleteByPeriod(Period.MONTHLY);
        when(leaderboardRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventProducer).publishLeaderboardUpdated(any(), anyInt());

        leaderboardService.generateLeaderboard(Period.MONTHLY);

        verify(leaderboardRepository, times(1)).deleteByPeriod(Period.MONTHLY);
        verify(leaderboardRepository, times(1)).saveAll(any());
        verify(eventProducer, times(1)).publishLeaderboardUpdated(Period.MONTHLY, 2);
    }

    @Test
    void getLeaderboard_ShouldReturnPaginatedLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10);
        
        LeaderBoard entry = new LeaderBoard();
        entry.setLeaderboardId(UUID.randomUUID());
        entry.setUserId(userId1);
        entry.setPoints(100);
        entry.setRank(1);
        entry.setPeriod(Period.MONTHLY);
        entry.setGeneratedAt(new Date());

        Page<LeaderBoard> leaderboardPage = new PageImpl<>(List.of(entry));
        
        LeaderboardResponseDTO dto = LeaderboardResponseDTO.builder()
                .leaderboardId(entry.getLeaderboardId())
                .userId(userId1)
                .points(100)
                .rank(1)
                .period(Period.MONTHLY)
                .build();

        when(leaderboardRepository.findByPeriodOrderByRankAsc(Period.MONTHLY, pageable))
                .thenReturn(leaderboardPage);
        when(leaderboardMapper.toLeaderboardResponseDTO(any())).thenReturn(dto);

        Page<LeaderboardResponseDTO> response = leaderboardService.getLeaderboard(Period.MONTHLY, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().get(0).getRank());
    }

    @Test
    void getTopContributors_ShouldReturnTopUsers() {
        LeaderBoard entry1 = new LeaderBoard();
        entry1.setUserId(userId1);
        entry1.setPoints(200);
        entry1.setRank(1);
        entry1.setPeriod(Period.MONTHLY);

        LeaderBoard entry2 = new LeaderBoard();
        entry2.setUserId(userId2);
        entry2.setPoints(100);
        entry2.setRank(2);
        entry2.setPeriod(Period.MONTHLY);

        LeaderboardResponseDTO dto1 = LeaderboardResponseDTO.builder()
                .userId(userId1)
                .points(200)
                .rank(1)
                .build();

        LeaderboardResponseDTO dto2 = LeaderboardResponseDTO.builder()
                .userId(userId2)
                .points(100)
                .rank(2)
                .build();

        when(leaderboardRepository.findTopContributorsByPeriod(eq(Period.MONTHLY), any(Pageable.class)))
                .thenReturn(List.of(entry1, entry2));
        when(leaderboardMapper.toLeaderboardResponseDTO(entry1)).thenReturn(dto1);
        when(leaderboardMapper.toLeaderboardResponseDTO(entry2)).thenReturn(dto2);

        List<LeaderboardResponseDTO> response = leaderboardService.getTopContributors(Period.MONTHLY, 10);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1, response.get(0).getRank());
        assertEquals(200, response.get(0).getPoints());
    }
}
