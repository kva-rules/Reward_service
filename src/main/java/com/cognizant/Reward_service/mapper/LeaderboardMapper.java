package com.cognizant.Reward_service.mapper;

import com.cognizant.Reward_service.domain.LeaderBoard;
import com.cognizant.Reward_service.dto.response.LeaderboardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaderboardMapper {
    
    @Mapping(target = "leaderboardId", source = "leaderboardId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "points", source = "points")
    @Mapping(target = "rank", source = "rank")
    @Mapping(target = "period", source = "period")
    @Mapping(target = "generatedAt", source = "generatedAt")
    LeaderboardResponseDTO toLeaderboardResponseDTO(LeaderBoard leaderBoard);
}
