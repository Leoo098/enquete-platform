package com.project.enquete.core.enquete_platform.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameForm {

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 20, message = "O nome de usuário deve ter entre 3 e 20 caracteres")
    private String username;

    public UsernameForm(String username) {
        this.username = username;
    }
}
