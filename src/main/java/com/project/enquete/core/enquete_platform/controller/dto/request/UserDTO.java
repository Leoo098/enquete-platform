package com.project.enquete.core.enquete_platform.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserDTO(UUID id,
                      @NotBlank(message = "Campo obrigatório")
                      String email,
                      @NotBlank(message = "Campo obrigatório")
                      String password) {

}
