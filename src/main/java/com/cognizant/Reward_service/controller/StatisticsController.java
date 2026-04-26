package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.response.StatisticsResponseDTO;
import com.cognizant.Reward_service.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reward Statistics", description = "Aggregate counts for analytics dashboards")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", description = "Retrieve overall reward statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StatisticsResponseDTO> getStatistics() {
        log.info("GET /api/rewards/statistics");
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
