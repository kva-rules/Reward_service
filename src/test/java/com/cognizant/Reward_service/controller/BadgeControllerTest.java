package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.BadgeAssignRequestDTO;
import com.cognizant.Reward_service.dto.request.BadgeRequestDTO;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import com.cognizant.Reward_service.dto.response.UserBadgeResponseDTO;
import com.cognizant.Reward_service.enums.badgeName;
import com.cognizant.Reward_service.security.JwtTokenProvider;
import com.cognizant.Reward_service.service.BadgeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BadgeController.class)
class BadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID userId;
    private UUID badgeId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        badgeId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getUserBadges_ShouldReturnBadges() throws Exception {
        UserBadgeResponseDTO badge = UserBadgeResponseDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .badgeId(badgeId)
                .badgeName(badgeName.FIRST_SOLVER)
                .badgeDescription("First solution approved")
                .earnedAt(new Date())
                .build();

        when(badgeService.getUserBadges(userId)).thenReturn(List.of(badge));

        mockMvc.perform(get("/api/rewards/users/{userId}/badges", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeName").value("FIRST_SOLVER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignBadge_WithAdminRole_ShouldAssignBadge() throws Exception {
        BadgeAssignRequestDTO request = BadgeAssignRequestDTO.builder()
                .userId(userId)
                .badgeId(badgeId)
                .build();

        UserBadgeResponseDTO response = UserBadgeResponseDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .badgeId(badgeId)
                .badgeName(badgeName.FIRST_SOLVER)
                .earnedAt(new Date())
                .build();

        when(badgeService.assignBadge(any(BadgeAssignRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/rewards/badges/assign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.badgeName").value("FIRST_SOLVER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBadge_WithAdminRole_ShouldCreateBadge() throws Exception {
        BadgeRequestDTO request = BadgeRequestDTO.builder()
                .badgeName(badgeName.TOP_CONTRIBUTOR)
                .description("Top contributor badge")
                .pointsRequired(500)
                .build();

        BadgeResponseDTO response = BadgeResponseDTO.builder()
                .badgeId(badgeId)
                .badgeName(badgeName.TOP_CONTRIBUTOR)
                .description("Top contributor badge")
                .pointsRequired(500)
                .createdAt(new Date())
                .build();

        when(badgeService.createBadge(any(BadgeRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/rewards/badges")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.badgeName").value("TOP_CONTRIBUTOR"));
    }

    @Test
    @WithMockUser(roles = "ENGINEER")
    void getAllBadges_ShouldReturnAllBadges() throws Exception {
        BadgeResponseDTO badge = BadgeResponseDTO.builder()
                .badgeId(badgeId)
                .badgeName(badgeName.FIRST_SOLVER)
                .description("First solution approved")
                .pointsRequired(100)
                .build();

        when(badgeService.getAllBadges()).thenReturn(List.of(badge));

        mockMvc.perform(get("/api/rewards/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeName").value("FIRST_SOLVER"));
    }
}
