package com.cognizant.Reward_service.kafka;

import com.cognizant.Reward_service.dto.event.RewardEventDTO;
import com.cognizant.Reward_service.service.RewardEventProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardEventConsumerTest {

    @Mock
    private RewardEventProcessor eventProcessor;

    @InjectMocks
    private RewardEventConsumer consumer;

    private RewardEventDTO event;

    @BeforeEach
    void setUp() {
        event = RewardEventDTO.builder()
                .userId(UUID.randomUUID())
                .referenceId(UUID.randomUUID())
                .referenceType("TICKET")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Test
    void consumeTicketResolved_ShouldProcessEvent() {
        doNothing().when(eventProcessor).processEvent(any(RewardEventDTO.class));

        consumer.consumeTicketResolved(event);

        verify(eventProcessor, times(1)).processEvent(any(RewardEventDTO.class));
    }

    @Test
    void consumeSolutionApproved_ShouldProcessEvent() {
        doNothing().when(eventProcessor).processEvent(any(RewardEventDTO.class));

        consumer.consumeSolutionApproved(event);

        verify(eventProcessor, times(1)).processEvent(any(RewardEventDTO.class));
    }

    @Test
    void consumeKnowledgeCreated_ShouldProcessEvent() {
        doNothing().when(eventProcessor).processEvent(any(RewardEventDTO.class));

        consumer.consumeKnowledgeCreated(event);

        verify(eventProcessor, times(1)).processEvent(any(RewardEventDTO.class));
    }

    @Test
    void consumeSolutionVoted_ShouldProcessEvent() {
        doNothing().when(eventProcessor).processEvent(any(RewardEventDTO.class));

        consumer.consumeSolutionVoted(event);

        verify(eventProcessor, times(1)).processEvent(any(RewardEventDTO.class));
    }

    @Test
    void consumeKnowledgeRated_ShouldProcessEvent() {
        doNothing().when(eventProcessor).processEvent(any(RewardEventDTO.class));

        consumer.consumeKnowledgeRated(event);

        verify(eventProcessor, times(1)).processEvent(any(RewardEventDTO.class));
    }
}
