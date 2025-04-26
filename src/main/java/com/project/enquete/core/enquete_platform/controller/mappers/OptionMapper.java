package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.controller.dto.request.OptionDTO;
import com.project.enquete.core.enquete_platform.model.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(target = "poll", ignore = true)
    Option toEntity(OptionDTO optionDTO);

    @Mapping(target = "pollId", source = "poll.id")
    OptionDTO toDTO(Option option);

    List<Option> toEntityList(List<OptionDTO> optionDTOs);

    List<OptionDTO> toDTOList(List<Option> options);

}
