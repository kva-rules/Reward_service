package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.LeaderboardGenerateRequestDTO;
import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import com.cognizant.Reward_service.enums.Period;
import com.cognizant.Reward_service.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leaderboard", description = "Leaderboard management APIs")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", description = "Retrieve paginated leaderboard for a period")
    public ResponseEntity<Page<LeaderboardResponseDTO>> getLeaderboard(
            @RequestParam(defaultValue = "MONTHLY") Period period,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/rewards/leaderboard for period: {}", period);
        return ResponseEntity.ok(leaderboardService.getLeaderboard(period, pageable));
    }

    @PostMapping("/leaderboard/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate leaderboard", description = "Generate leaderboard for a period (Admin only)")
    public ResponseEntity<Void> generateLeaderboard(@Valid @RequestBody LeaderboardGenerateRequestDTO request) {
        log.info("POST /api/rewards/leaderboard/generate for period: {}", request.getPeriod());
        leaderboardService.generateLeaderboard(request.getPeriod());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top-contributors")
    @Operation(summary = "Get top contributors", description = "Retrieve top contributors for a period")
    public ResponseEntity<List<LeaderboardResponseDTO>> getTopContributors(
            @RequestParam(defaultValue = "MONTHLY") Period period,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/rewards/top-contributors for period: {} limit: {}", period, limit);
        return ResponseEntity.ok(leaderboardService.getTopContributors(period, limit));
    }
}
