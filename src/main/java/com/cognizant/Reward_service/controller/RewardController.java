package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import com.cognizant.Reward_service.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reward", description = "Reward management APIs")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/users/{userId}/points")
    @Operation(summary = "Get user points", description = "Retrieve total points for a user")
    public ResponseEntity<RewardResponseDTO> getUserPoints(@PathVariable UUID userId) {
        log.info("GET /api/rewards/users/{}/points", userId);
        return ResponseEntity.ok(rewardService.getUserPoints(userId));
    }

    @GetMapping("/users/{userId}/transactions")
    @Operation(summary = "Get user transactions", description = "Retrieve paginated transactions for a user")
    public ResponseEntity<Page<TransactionResponseDTO>> getUserTransactions(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/rewards/users/{}/transactions", userId);
        return ResponseEntity.ok(rewardService.getTransactions(userId, pageable));
    }

    @PostMapping("/points")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add points", description = "Add points to a user (Admin only)")
    public ResponseEntity<RewardResponseDTO> addPoints(@Valid @RequestBody RewardRequestDTO request) {
        log.info("POST /api/rewards/points for user: {}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rewardService.addPoints(request));
    }

    @GetMapping("/users/{userId}/contributions")
    @Operation(summary = "Get contribution summary", description = "Retrieve contribution summary for a user")
    public ResponseEntity<ContributionSummaryDTO> getContributionSummary(@PathVariable UUID userId) {
        log.info("GET /api/rewards/users/{}/contributions", userId);
        return ResponseEntity.ok(rewardService.getContributionSummary(userId));
    }
}
