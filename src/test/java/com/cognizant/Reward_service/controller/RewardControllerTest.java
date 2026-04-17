package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import com.cognizant.Reward_service.security.JwtTokenProvider;
import com.cognizant.Reward_service.service.RewardService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RewardService rewardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID userId;
    private RewardResponseDTO rewardResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        rewardResponse = RewardResponseDTO.builder()
                .userId(userId)
                .totalPoints(100)
                .build();
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getUserPoints_ShouldReturnPoints() throws Exception {
        when(rewardService.getUserPoints(userId)).thenReturn(rewardResponse);

        mockMvc.perform(get("/api/rewards/users/{userId}/points", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.totalPoints").value(100));
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getUserTransactions_ShouldReturnTransactions() throws Exception {
        TransactionResponseDTO transaction = TransactionResponseDTO.builder()
                .transactionId(UUID.randomUUID())
                .userId(userId)
                .points(50)
                .reason("TICKET_RESOLVED")
                .build();

        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(transaction));
        when(rewardService.getTransactions(any(UUID.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/rewards/users/{userId}/transactions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].points").value(50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addPoints_WithAdminRole_ShouldAddPoints() throws Exception {
        RewardRequestDTO request = RewardRequestDTO.builder()
                .userId(userId)
                .points(50)
                .reason("TICKET_RESOLVED")
                .referenceId(UUID.randomUUID())
                .build();

        RewardResponseDTO response = RewardResponseDTO.builder()
                .userId(userId)
                .totalPoints(150)
                .build();

        when(rewardService.addPoints(any(RewardRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/rewards/points")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPoints").value(150));
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void addPoints_WithEngineerRole_ShouldBeForbidden() throws Exception {
        RewardRequestDTO request = RewardRequestDTO.builder()
                .userId(userId)
                .points(50)
                .reason("TICKET_RESOLVED")
                .build();

        mockMvc.perform(post("/api/rewards/points")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getContributionSummary_ShouldReturnSummary() throws Exception {
        ContributionSummaryDTO summary = ContributionSummaryDTO.builder()
                .summaryId(UUID.randomUUID())
                .userId(userId)
                .ticketsResolved(10)
                .solutionsApproved(5)
                .articlesCreated(3)
                .upvotesReceived(20)
                .build();

        when(rewardService.getContributionSummary(userId)).thenReturn(summary);

        mockMvc.perform(get("/api/rewards/users/{userId}/contributions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketsResolved").value(10))
                .andExpect(jsonPath("$.solutionsApproved").value(5));
    }

    @Test
    void getUserPoints_WithoutAuth_ShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/rewards/users/{userId}/points", userId))
                .andExpect(status().isUnauthorized());
    }
}
