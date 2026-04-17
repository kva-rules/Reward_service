package com.cognizant.Reward_service.dto.response;

import com.cognizant.Reward_service.enums.referenceTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    
    private UUID transactionId;
    private UUID userId;
    private Integer points;
    private String reason;
    private UUID referenceId;
    private referenceTypes referenceType;
    private Date createdAt;
}
