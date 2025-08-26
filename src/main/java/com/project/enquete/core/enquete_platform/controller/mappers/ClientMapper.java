package com.project.enquete.core.enquete_platform.controller.mappers;

import com.project.enquete.core.enquete_platform.dto.request.ClientDTO;
import com.project.enquete.core.enquete_platform.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientDTO dto);

    ClientDTO toDTO(Client client);
}
