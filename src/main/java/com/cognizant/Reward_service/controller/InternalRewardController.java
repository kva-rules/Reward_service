package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.event.RewardEventDTO;
import com.cognizant.Reward_service.dto.response.RewardResponseDTO;
import com.cognizant.Reward_service.service.RewardEventProcessor;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rewards (Internal)", description = "Service-to-service reward mutations")
public class InternalRewardController {

    private final RewardEventProcessor eventProcessor;
    private final RewardService rewardService;

    @PostMapping("/event")
    @Operation(summary = "Process reward event", description = "Process a reward event from another service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event processed"),
            @ApiResponse(responseCode = "400", description = "Invalid event payload")
    })
    public ResponseEntity<Void> processEvent(@Valid @RequestBody RewardEventDTO event) {
        log.info("POST /internal/rewards/event - Processing event: {} for user: {}",
                event.getEventType(), event.getUserId());
        eventProcessor.processEvent(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user reward info", description = "Get user reward information for internal use")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reward info returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<RewardResponseDTO> getUserRewardInfo(
            @Parameter(description = "User UUID") @PathVariable UUID userId) {
        log.info("GET /internal/rewards/users/{}", userId);
        return ResponseEntity.ok(rewardService.getUserPoints(userId));
    }
}
