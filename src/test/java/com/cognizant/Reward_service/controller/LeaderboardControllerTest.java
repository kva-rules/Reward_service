package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.LeaderboardGenerateRequestDTO;
import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.security.JwtTokenProvider;
import com.cognizant.Reward_service.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeaderboardService leaderboardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getLeaderboard_ShouldReturnLeaderboard() throws Exception {
        LeaderboardResponseDTO entry = LeaderboardResponseDTO.builder()
                .leaderboardId(UUID.randomUUID())
                .userId(userId)
                .points(100)
                .rank(1)
                .period(Period.MONTHLY)
                .generatedAt(new Date())
                .build();

        Page<LeaderboardResponseDTO> page = new PageImpl<>(List.of(entry));
        when(leaderboardService.getLeaderboard(eq(Period.MONTHLY), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/rewards/leaderboard")
                        .param("period", "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rank").value(1))
                .andExpect(jsonPath("$.content[0].points").value(100));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void generateLeaderboard_WithAdminRole_ShouldGenerate() throws Exception {
        LeaderboardGenerateRequestDTO request = LeaderboardGenerateRequestDTO.builder()
                .period(Period.MONTHLY)
                .build();

        doNothing().when(leaderboardService).generateLeaderboard(Period.MONTHLY);

        mockMvc.perform(post("/api/rewards/leaderboard/generate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void generateLeaderboard_WithEngineerRole_ShouldBeForbidden() throws Exception {
        LeaderboardGenerateRequestDTO request = LeaderboardGenerateRequestDTO.builder()
                .period(Period.MONTHLY)
                .build();

        mockMvc.perform(post("/api/rewards/leaderboard/generate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getTopContributors_ShouldReturnTopUsers() throws Exception {
        LeaderboardResponseDTO entry1 = LeaderboardResponseDTO.builder()
                .userId(userId)
                .points(200)
                .rank(1)
                .period(Period.MONTHLY)
                .build();

        when(leaderboardService.getTopContributors(Period.MONTHLY, 10)).thenReturn(List.of(entry1));

        mockMvc.perform(get("/api/rewards/top-contributors")
                        .param("period", "MONTHLY")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].points").value(200));
    }
}
