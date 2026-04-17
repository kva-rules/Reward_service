package com.cognizant.Reward_service.service;

import com.cognizant.Reward_service.dto.event.RewardEventDTO;

public interface RewardEventProcessor {
    void processEvent(RewardEventDTO event);
}
