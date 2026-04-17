package com.cognizant.Reward_service.mapper;

import com.cognizant.Reward_service.domain.Badges;
import com.cognizant.Reward_service.dto.response.BadgeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BadgeMapper {
    
    @Mapping(target = "badgeId", source = "badgeId")
    @Mapping(target = "badgeName", source = "badgeName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "pointsRequired", source = "pointsRequired")
    @Mapping(target = "createdAt", source = "createdAt")
    BadgeResponseDTO toBadgeResponseDTO(Badges badge);
}
