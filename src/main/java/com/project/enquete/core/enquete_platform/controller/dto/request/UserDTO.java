package com.project.enquete.core.enquete_platform.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDTO(UUID id,
                      @NotBlank(message = "Campo obrigatório")
                      @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
                      String username,
                      @NotBlank(message = "Campo obrigatório")
                      @Email(message = "inválido")
                      String email,
                      @NotBlank(message = "Campo obrigatório")
                      String password) {
}
