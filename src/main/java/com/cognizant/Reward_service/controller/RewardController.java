package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.RewardRequestDTO;
import com.cognizant.Reward_service.dto.response.ContributionSummaryDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.dto.response.TransactionResponseDTO;
import com.cognizant.Reward_service.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Rewards", description = "Points and reward history per user")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/users/{userId}/points")
    @Operation(summary = "Get user points", description = "Retrieve total points for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RewardResponseDTO> getUserPoints(
            @Parameter(description = "User UUID") @PathVariable UUID userId) {
        log.info("GET /api/rewards/users/{}/points", userId);
        return ResponseEntity.ok(rewardService.getUserPoints(userId));
    }

    @GetMapping("/users/{userId}/transactions")
    @Operation(summary = "Get user transactions", description = "Retrieve paginated transactions for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<TransactionResponseDTO>> getUserTransactions(
            @Parameter(description = "User UUID") @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/rewards/users/{}/transactions", userId);
        return ResponseEntity.ok(rewardService.getTransactions(userId, pageable));
    }

    @PostMapping("/points")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add points", description = "Add points to a user (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Points added"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RewardResponseDTO> addPoints(@Valid @RequestBody RewardRequestDTO request) {
        log.info("POST /api/rewards/points for user: {}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rewardService.addPoints(request));
    }

    @GetMapping("/users/{userId}/contributions")
    @Operation(summary = "Get contribution summary", description = "Retrieve contribution summary for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ContributionSummaryDTO> getContributionSummary(
            @Parameter(description = "User UUID") @PathVariable UUID userId) {
        log.info("GET /api/rewards/users/{}/contributions", userId);
        return ResponseEntity.ok(rewardService.getContributionSummary(userId));
    }
}
