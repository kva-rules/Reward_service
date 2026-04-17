package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.request.BadgeAssignRequestDTO;
import com.cognizant.Reward_service.dto.request.BadgeRequestDTO;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import com.cognizant.Reward_service.dto.response.UserBadgeResponseDTO;
import com.cognizant.Reward_service.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Badge", description = "Badge management APIs")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/users/{userId}/badges")
    @Operation(summary = "Get user badges", description = "Retrieve all badges earned by a user")
    public ResponseEntity<List<UserBadgeResponseDTO>> getUserBadges(@PathVariable UUID userId) {
        log.info("GET /api/rewards/users/{}/badges", userId);
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }

    @PostMapping("/badges/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign badge", description = "Manually assign a badge to a user (Admin only)")
    public ResponseEntity<UserBadgeResponseDTO> assignBadge(@Valid @RequestBody BadgeAssignRequestDTO request) {
        log.info("POST /api/rewards/badges/assign for user: {}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(badgeService.assignBadge(request));
    }

    @PostMapping("/badges")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create badge", description = "Create a new badge (Admin only)")
    public ResponseEntity<BadgeResponseDTO> createBadge(@Valid @RequestBody BadgeRequestDTO request) {
        log.info("POST /api/rewards/badges - Creating badge: {}", request.getBadgeName());
        return ResponseEntity.status(HttpStatus.CREATED).body(badgeService.createBadge(request));
    }

    @GetMapping("/badges")
    @Operation(summary = "Get all badges", description = "Retrieve all available badges")
    public ResponseEntity<List<BadgeResponseDTO>> getAllBadges() {
        log.info("GET /api/rewards/badges");
        return ResponseEntity.ok(badgeService.getAllBadges());
    }
}
