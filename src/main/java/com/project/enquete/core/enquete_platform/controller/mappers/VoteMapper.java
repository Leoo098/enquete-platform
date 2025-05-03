package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.controller.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.model.Vote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    Vote toEntity(VoteDTO dto);

    VoteDTO toDTO(Vote vote);
}
