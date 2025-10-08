package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.TimeUnit;
import com.project.enquete.core.enquete_platform.model.Vote;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PollMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "expiresAt", source = ".", qualifiedByName = "calculateExpiration")
    @Mapping(target = "options", source = "options")
    @Mapping(target = "createdBy", ignore = true)
    Poll toEntity(PollDTO pollDTO);

    @Mapping(target = "id", source = "poll.id")
    @Mapping(target = "question", source = "poll.question")
    @Mapping(target = "createdAt", source = "poll.createdAt")
    @Mapping(target = "expiresAt", source = "poll.expiresAt")
    @Mapping(target = "options", source = "options")
    @Mapping(target = "createdBy", source = "poll.createdBy.username")
    PollResponseDTO toResponseDTO(Poll poll, @Context PollDTO requestDTO);

    @Named("calculateExpiration")
    default Instant calculateExpiration(PollDTO pollDTO) {
        return Instant.now().plus(
                pollDTO.duration(),
                toChronoUnit(pollDTO.timeUnit())
        );
    }

    default Duration calculateTimeLeft(Instant expiresAt) {
        return Duration.between(Instant.now(), expiresAt);
    }

    default Integer mapVotesToInteger(List<Vote> votes) {
        return votes != null ? votes.size() : 0;
    }

    default ChronoUnit toChronoUnit(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case MINUTES -> ChronoUnit.MINUTES;
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
        };
    }
}
