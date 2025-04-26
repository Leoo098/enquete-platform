package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.controller.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.TimeUnit;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface PollMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "expiresAt", source = ".", qualifiedByName = "calculateExpiration")
    @Mapping(target = "options", source = "options")
    @Mapping(target = "createdBy", ignore = true)
    Poll toEntity(PollDTO pollDTO);

    @Mapping(target = "id", source = "poll.id")
    @Mapping(target = "options", source = "options")
    PollResponseDTO toResponseDTO(Poll poll, @Context PollDTO requestDTO);

    @Named("calculateExpiration")
    default Instant calculateExpiration(PollDTO pollDTO) {
        return Instant.now().plus(
                pollDTO.duration(),
                toChronoUnit(pollDTO.timeUnit())
        );
    }

    default ChronoUnit toChronoUnit(TimeUnit timeUnit) {
        return switch (timeUnit) {
            case MINUTES -> ChronoUnit.MINUTES;
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
            case STOP -> ChronoUnit.valueOf(Instant.now().toString());
        };
    }
}
