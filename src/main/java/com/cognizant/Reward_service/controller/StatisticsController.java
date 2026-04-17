package com.cognizant.Reward_service.controller;

import com.cognizant.Reward_service.dto.response.StatisticsResponseDTO;
import com.cognizant.Reward_service.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Statistics", description = "Reward statistics APIs")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", description = "Retrieve overall reward statistics")
    public ResponseEntity<StatisticsResponseDTO> getStatistics() {
        log.info("GET /api/rewards/statistics");
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
